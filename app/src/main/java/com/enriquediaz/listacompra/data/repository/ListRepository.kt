package com.enriquediaz.listacompra.data.repository

import android.util.Log
import com.enriquediaz.listacompra.data.model.Category
import com.enriquediaz.listacompra.data.model.Product
import com.enriquediaz.listacompra.data.model.ShoppingList
import com.enriquediaz.listacompra.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun createList(name: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: throw Exception("No hay usuario logueado")
            val newList = ShoppingList(name = name, ownerId = currentUserId, members = listOf(currentUserId))
            val listRef = firestore.collection("lists").add(newList).await()

            val defaultCategory = Category(name = com.enriquediaz.listacompra.utils.AutoCategorizer.DEFAULT_CATEGORY_NAME)
            firestore.collection("lists").document(listRef.id).collection("categories").add(defaultCategory).await()

            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addCategory(listId: String, categoryName: String): Result<Unit> {
        return try {
            val newCategory = Category(name = categoryName)
            firestore.collection("lists").document(listId).collection("categories").add(newCategory).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // NUEVO: Borrar categoría
    suspend fun deleteCategory(listId: String, categoryId: String): Result<Unit> {
        return try {
            withContext(NonCancellable) {
                firestore.collection("lists").document(listId)
                    .collection("categories").document(categoryId)
                    .delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error borrando categoría: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun addProduct(
        listId: String, productName: String, categoryId: String,
        ean: String? = null, price: Double? = null, imageUrl: String? = null
    ): Result<Unit> {
        return try {
            val newProduct = Product(
                name = productName, categoryId = categoryId, isCompleted = false,
                ean = ean, price = price, imageUrl = imageUrl
            )
            firestore.collection("lists").document(listId).collection("products").add(newProduct).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateProduct(listId: String, productId: String, newName: String, newCategoryId: String): Result<Unit> {
        return try {
            val updates = mapOf("name" to newName, "categoryId" to newCategoryId)
            firestore.collection("lists").document(listId)
                .collection("products").document(productId)
                .update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error actualizando producto: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun toggleProductCompleted(listId: String, productId: String, isCompleted: Boolean): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>("isCompleted" to isCompleted)
            withContext(NonCancellable) {
                firestore.collection("lists").document(listId).collection("products").document(productId)
                    .set(updates, SetOptions.merge()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteProduct(listId: String, productId: String): Result<Unit> {
        return try {
            withContext(NonCancellable) {
                firestore.collection("lists").document(listId).collection("products").document(productId).delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteList(listId: String): Result<Unit> {
        return try {
            withContext(NonCancellable) {
                val batch = firestore.batch()
                val productsSnapshot = firestore.collection("lists").document(listId).collection("products").get().await()
                productsSnapshot.documents.forEach { batch.delete(it.reference) }
                val categoriesSnapshot = firestore.collection("lists").document(listId).collection("categories").get().await()
                categoriesSnapshot.documents.forEach { batch.delete(it.reference) }
                batch.delete(firestore.collection("lists").document(listId))
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun shareListWithEmail(listId: String, email: String): Result<Unit> {
        return try {
            val cleanEmail = email.trim().lowercase()
            val userSnapshot = firestore.collection("users").whereEqualTo("email", cleanEmail).get().await()
            if (userSnapshot.isEmpty) throw Exception("No existe ningún usuario con ese correo.")
            val invitedUserUid = userSnapshot.documents.first().id
            firestore.collection("lists").document(listId).update("members", FieldValue.arrayUnion(invitedUserUid)).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun removeMember(listId: String, memberUid: String): Result<Unit> {
        return try {
            withContext(NonCancellable) {
                firestore.collection("lists").document(listId).update("members", FieldValue.arrayRemove(memberUid)).await()
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getListById(listId: String): Result<ShoppingList> {
        return try {
            val doc = firestore.collection("lists").document(listId).get().await()
            val list = doc.toObject(ShoppingList::class.java)?.copy(id = doc.id) ?: throw Exception("Lista no encontrada")
            Result.success(list)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUserDetails(uid: String): Result<User> {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java) ?: User(uid = uid, email = "Desconocido", name = "Usuario")
            Result.success(user)
        } catch (e: Exception) { Result.failure(e) }
    }

    fun getUserListsFlow(): Flow<List<ShoppingList>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: run { close(Exception("No hay usuario")); return@callbackFlow }
        val listener = firestore.collection("lists")
            .whereArrayContains("members", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) {
                    val lists = snapshot.documents.map { doc ->
                        doc.toObject(ShoppingList::class.java)?.copy(id = doc.id) ?: ShoppingList()
                    }
                    trySend(lists)
                }
            }
        awaitClose { listener.remove() }
    }

    fun getProductsFlow(listId: String): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("lists").document(listId).collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) {
                    val products = snapshot.documents.map { doc ->
                        Product(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            categoryId = doc.getString("categoryId") ?: "",
                            isCompleted = doc.getBoolean("isCompleted") ?: false,
                            tags = (doc.get("tags") as? List<String>) ?: emptyList(),
                            ean = doc.getString("ean"),
                            price = doc.getDouble("price"),
                            imageUrl = doc.getString("imageUrl")
                        )
                    }
                    trySend(products)
                }
            }
        awaitClose { listener.remove() }
    }

    fun getCategoriesFlow(listId: String): Flow<List<Category>> = callbackFlow {
        val listener = firestore.collection("lists").document(listId).collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) {
                    val categories = snapshot.documents.map { doc ->
                        doc.toObject(Category::class.java)?.copy(id = doc.id) ?: Category()
                    }
                    trySend(categories)
                }
            }
        awaitClose { listener.remove() }
    }
    suspend fun updateProduct(listId: String, productId: String, newName: String, newCategoryId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "name" to newName,
                "categoryId" to newCategoryId
            )
            firestore.collection("lists").document(listId)
                .collection("products").document(productId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error actualizando producto: ${e.message}")
            Result.failure(e)
        }
    }

    // Eliminar una categoría moviendo sus productos a la categoría por defecto
    suspend fun deleteCategory(listId: String, categoryIdToDelete: String, defaultCategoryId: String): Result<Unit> {
        return try {
            withContext(NonCancellable) {
                val batch = firestore.batch()

                // 1. Buscar todos los productos que pertenecen a la categoría a borrar
                val productsSnapshot = firestore.collection("lists").document(listId)
                    .collection("products")
                    .whereEqualTo("categoryId", categoryIdToDelete)
                    .get()
                    .await()

                // 2. Mover esos productos a la categoría por defecto
                productsSnapshot.documents.forEach { doc ->
                    batch.update(doc.reference, "categoryId", defaultCategoryId)
                }

                // 3. Borrar el documento de la categoría
                val categoryRef = firestore.collection("lists").document(listId)
                    .collection("categories").document(categoryIdToDelete)
                batch.delete(categoryRef)

                // Ejecutar todo de golpe
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Error borrando categoría: ${e.message}")
            Result.failure(e)
        }
    }
}