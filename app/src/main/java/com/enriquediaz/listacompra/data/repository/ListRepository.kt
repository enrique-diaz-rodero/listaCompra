package com.enriquediaz.listacompra.data.repository

import com.enriquediaz.listacompra.data.model.ShoppingList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // Obtener listas donde el usuario está en 'members'
    suspend fun getUserLists(): Result<List<ShoppingList>> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: throw Exception("No hay usuario logueado")
            // Es la consulta a la BD para obtener las listas del usuario
            val snapshot = firestore.collection("lists")
                .whereArrayContains("members", currentUserId)
                .get()
                .await()
            // Convierte los documentos de la BD en objetos de la clase ShoppingList
            val lists = snapshot.toObjects(ShoppingList::class.java)

            // Muestra las listas si ha tenido éxito, sino muestra el error
            Result.success(lists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Crear lista nueva
    suspend fun createList(name: String): Result<Unit> {
        return try {
            // Comprueba que haya un usuario logueado para poder crear las listas
            val currentUserId = auth.currentUser?.uid ?: throw Exception("No hay usuario logueado")
            // Crea una nueva lista con las propiedades de la clase ShoppingList
            val newList = ShoppingList(
                name = name,
                ownerId = currentUserId,
                members = listOf(currentUserId) // El creador es el primer miembro
            )
            // Se guarda en la BD la nueva lista
            firestore.collection("lists").add(newList).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
