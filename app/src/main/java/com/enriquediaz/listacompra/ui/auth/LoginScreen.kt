package com.enriquediaz.listacompra.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bienvenido a Lista Compra", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.loginAnonimo {
                    // Cuando el login termina, navegamos al dashboard
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true } // borra el login del historial
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Entrar como Anónimo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Lógica de Google */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = false // Deshabilitado hasta que lo implementemos
        ) {
            Text("Entrar con Google (Próximamente)")
        }
    }
}