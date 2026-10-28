package com.enriquediaz.listacompra.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore // <-- AÑADIDO: Import de Firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore // <-- AÑADIDO: Inyectamos Firestore
) {
    // Login de forma anónima
    suspend fun signInAnonymously(): Result<Unit> {
        return try {
            auth.signInAnonymously().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login con la cuenta de Google
    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Guardar los datos del usuario para poder buscarle luego
    suspend fun saveUserToFirestore(user: FirebaseUser): Result<Unit> {
        return try {
            val userData = mapOf(
                "email" to (user.email ?: ""),
                "name" to (user.displayName ?: "Usuario")
            )
            // Usamos el UID como ID del documento para que sea único
            firestore.collection("users").document(user.uid).set(userData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
