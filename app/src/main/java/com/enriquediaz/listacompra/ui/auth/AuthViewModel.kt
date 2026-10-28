package com.enriquediaz.listacompra.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquediaz.listacompra.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun loginAnonimo(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signInAnonymously()
            if (result.isSuccess) {
                // NUEVO: Guardamos al usuario anónimo en Firestore
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    authRepository.saveUserToFirestore(currentUser)
                }
                onSuccess()
            } else {
                Log.e("AuthViewModel", "Error en login anónimo: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun loginConGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            if (result.isSuccess) {
                // NUEVO: Guardamos al usuario de Google en Firestore
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    authRepository.saveUserToFirestore(currentUser)
                }
                onSuccess()
            } else {
                val mensajeError = result.exceptionOrNull()?.message ?: "Error desconocido en Google"
                onError(mensajeError)
            }
        }
    }
}