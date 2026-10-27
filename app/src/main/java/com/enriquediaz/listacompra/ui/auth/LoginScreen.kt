package com.enriquediaz.listacompra.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

// 1. TU WEB CLIENT ID (Sacado del google-services.json)
private const val WEB_CLIENT_ID = "1053111214079-tub2khuiltotgl0ap9ss3j6o5vpm4cu5.apps.googleusercontent.com"

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // 2. Herramientas necesarias para Google Sign-In
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    // 3. Estado para mostrar errores en pantalla
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bienvenido a Lista Compra", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // --- BOTÓN ANÓNIMO ---
        Button(
            onClick = {
                viewModel.loginAnonimo {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Entrar como Anónimo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTÓN DE GOOGLE ---
        Button(
            onClick = {
                errorMessage = null // Limpiamos errores anteriores

                scope.launch {
                    try {
                        // A. Configuramos qué queremos (pedir el token)
                        val googleIdOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID)
                            .build()

                        // B. Creamos la petición
                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        // C. Lanzamos el diálogo nativo de Android
                        val result = credentialManager.getCredential(
                            request = request,
                            context = context
                        )

                        // D. Procesamos la respuesta si el usuario elige una cuenta
                        val credential = result.credential
                        if (credential is androidx.credentials.CustomCredential &&
                            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        ) {
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                            // E. Le pasamos el token a nuestro ViewModel
                            viewModel.loginConGoogle(
                                idToken = googleIdTokenCredential.idToken,
                                onSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onError = { error ->
                                    errorMessage = error
                                }
                            )
                        }
                    } catch (e: GetCredentialException) {
                        // Si el usuario cierra la ventana sin elegir cuenta, o falla algo
                        errorMessage = "Error al iniciar sesión: ${e.message}"
                        Log.e("LoginScreen", "Error de Google", e)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar con Google")
        }

        // Mostrar mensaje de error si existe
        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}