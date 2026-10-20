package com.enriquediaz.listacompra.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ListDetailScreen(listId: String, navController: NavController){
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Detalle de la lista $listId")
    }
}