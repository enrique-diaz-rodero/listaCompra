package com.enriquediaz.listacompra.data.repository

import android.util.Log
import com.enriquediaz.listacompra.data.model.Category
import com.enriquediaz.listacompra.data.model.Product
import com.enriquediaz.listacompra.data.model.ShoppingList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source // IMPORTANTE: Para ignorar la caché
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun getUserLists(): Result<List<ShoppingList>> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: throw Exception("No hay usuario logueado")
            val snapshot =
                firestore.collection("lists").whereArrayContains("members", currentUserId).get()
                    .await()
            val lists = snapshot.documents.map { doc ->
                val list = doc.toObject(ShoppingList::class.java) ?: ShoppingList()
                list.copy(id = doc.id)
            }
            Result.success(lists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createList(name: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: throw Exception("No hay usuario logueado")
            val newList =
                ShoppingList(name = name, ownerId = currentUserId, members = listOf(currentUserId))
            firestore.collection("lists").add(newList).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(listId: String): Result<List<Category>> {
        return try {
            // Forzamos lectura desde el servidor
            val snapshot = firestore.collection("lists").document(listId).collection("categories")
                .get(Source.SERVER).await()
            val categories = snapshot.documents.map { doc ->
                val category = doc.toObject(Category::class.java) ?: Category()
                category.copy(id = doc.id)
            }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCategory(listId: String, categoryName: String): Result<Unit> {
        return try {
            val newCategory = Category(name = categoryName)
            firestore.collection("lists").document(listId).collection("categories").add(newCategory)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- LECTURA A PRUEBA DE BALAS ---
    suspend fun getProducts(listId: String): Result<List<Product>> {
        return try {
            // Leemos directamente del servidor para evitar cachés corruptas
            val snapshot = firestore.collection("lists")
                .document(listId)
                .collection("products")
                .get(Source.SERVER)
                .await()

            // Mapeo manual (evita bugs de Firestore al leer booleanos)
            val products = snapshot.documents.map { doc ->
                Product(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    categoryId = doc.getString("categoryId") ?: "",
                    isCompleted = doc.getBoolean("isCompleted") ?: false,
                    tags = (doc.get("tags") as? List<String>) ?: emptyList()
                )
            }
            Result.success(products)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error leyendo: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun addProduct(listId: String, productName: String, categoryId: String): Result<Unit> {
        return try {
            val newProduct =
                Product(name = productName, categoryId = categoryId, isCompleted = false)
            firestore.collection("lists").document(listId).collection("products").add(newProduct)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- ESCRITURA A PRUEBA DE BALAS ---
    suspend fun toggleProductCompleted(
        listId: String,
        productId: String,
        isCompleted: Boolean
    ): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>("isCompleted" to isCompleted)

            withContext(NonCancellable) {
                firestore.collection("lists")
                    .document(listId)
                    .collection("products")
                    .document(productId)
                    .set(updates, SetOptions.merge())
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error guardando: ${e.message}")
            Result.failure(e)
        }
    }

    // Eliminar un producto
    suspend fun deleteProduct(listId: String, productId: String): Result<Unit> {
        return try {
            withContext(NonCancellable) {
                firestore.collection("lists")
                    .document(listId)
                    .collection("products")
                    .document(productId)
                    .delete()
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error borrando producto: ${e.message}")
            Result.failure(e)
        }
    }

    // Eliminar una lista completa (y sus subcolecciones)
    suspend fun deleteList(listId: String): Result<Unit> {
        return try {
            withContext(NonCancellable) {
                // Usamos un Batch para hacer todas las operaciones de golpe (es más rápido y seguro)
                val batch = firestore.batch()

                // 1. Borrar todos los productos
                val productsSnapshot = firestore.collection("lists").document(listId).collection("products").get().await()
                productsSnapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }

                // 2. Borrar todas las categorías
                val categoriesSnapshot = firestore.collection("lists").document(listId).collection("categories").get().await()
                categoriesSnapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }

                // 3. Borrar la lista principal
                val listRef = firestore.collection("lists").document(listId)
                batch.delete(listRef)

                // Ejecutar todos los borrados
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error borrando la lista: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun shareListWithEmail(listId: String, email: String): Result<Unit> {
        return try {
            // 1. Buscamos al usuario por email en la colección "users"
            val userSnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (userSnapshot.isEmpty) {
                throw Exception("No existe ningún usuario con ese correo.")
            }

            val invitedUserUid = userSnapshot.documents.first().id

            // 2. Añadimos su UID al array "members" de la lista
            firestore.collection("lists")
                .document(listId)
                .update("members", FieldValue.arrayUnion(invitedUserUid))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error al compartir: ${e.message}")
            Result.failure(e)
        }
    }
}