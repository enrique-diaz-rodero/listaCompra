package com.enriquediaz.listacompra.ui.auth

import androidx.lifecycle.ViewModel
import com.enriquediaz.listacompra.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun loginAnonimo(onSuccess: () -> Unit) {
        val result = authRepository.signInAnonymously()
        if (result.isSucess){
            onSuccess()
        } else{
            // TODO:  Aquí podrías manejar el error (ej. mostrar un Toast)
        }
    }
    // TODO: // Aquí irá la lógica de Google más adelante
}