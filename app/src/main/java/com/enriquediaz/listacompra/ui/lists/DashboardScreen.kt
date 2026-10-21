package com.enriquediaz.listacompra.ui.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.enriquediaz.listacompra.data.model.ShoppingList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel() // Inyectamos el ViewModel con Hilt
) {
    // 1. Observamos el estado de las listas desde el ViewModel
    val lists by viewModel.lists.collectAsState()

    // 2. Estados locales para el diálogo de crear lista
    var showAddDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    // 3. Cargamos las listas la primera vez que se muestra la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadLists()
    }

    // 4. Estructura principal de la pantalla (Scaffold)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Listas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            // Botón flotante para añadir una nueva lista
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Lista")
            }
        }
    ) { paddingValues ->
        // 5. Contenido principal (la lista)
        if (lists.isEmpty()) {
            // Estado vacío
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes listas todavía.\n¡Crea una con el botón +!",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Lista de tarjetas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lists) { list ->
                    ListItemCard(
                        list = list,
                        onClick = {
                            // Navegamos al detalle pasando el ID de la lista en la URL
                            navController.navigate("list_detail/${list.id}")
                        }
                    )
                }
            }
        }
    }

    // 6. El Diálogo para crear una nueva lista
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                newListName = "" // Limpiamos el texto al cerrar
            },
            title = { Text("Nueva Lista") },
            text = {
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("Nombre de la lista") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newListName.isNotBlank()) {
                            viewModel.createNewList(newListName)
                            showAddDialog = false
                            newListName = ""
                        }
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    newListName = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// 7. Componente reutilizable para cada elemento de la lista
@Composable
fun ListItemCard(
    list: ShoppingList,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Compartida con: ${list.members.size} persona(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}