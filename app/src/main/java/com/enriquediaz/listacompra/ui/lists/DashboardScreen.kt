package com.enriquediaz.listacompra.ui.lists

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Pantalla de Login")
        Button(onClick = { navController.navigate("list_detail/123") }) {
            Text("Ir a una lista de prueba")
        }
    }
}
