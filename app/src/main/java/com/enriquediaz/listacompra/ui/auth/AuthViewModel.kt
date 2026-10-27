package com.enriquediaz.listacompra.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquediaz.listacompra.data.repository.AuthRepository
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
                onSuccess()
            } else {
                // TODO:  Aquí podrías manejar el error (ej. mostrar un Toast)
            }
        }
    }
    fun loginConGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            if (result.isSuccess) {
                onSuccess()
            } else {
                // Si falla, extraemos el mensaje de error y lo enviamos a la UI
                val mensajeError = result.exceptionOrNull()?.message ?: "Error desconocido en Google"
                onError(mensajeError)
            }
        }
    }
}