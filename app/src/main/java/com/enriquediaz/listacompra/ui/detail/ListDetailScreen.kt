package com.enriquediaz.listacompra.ui.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete // Icono de papelera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.PersonAdd // Icono de compartir

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
) // Añadido ExperimentalFoundationApi
@Composable
fun ListDetailScreen(
    navController: NavController,
    viewModel: ListDetailViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()

    var showCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    var showProductDialog by remember { mutableStateOf(false) }
    var newProductName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var emailToShareWith by remember { mutableStateOf("") }
    val shareResult by viewModel.shareResult.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de la compra") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showCategoryDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Nueva Categoría")
                    }
                    IconButton(onClick = {
                        showShareDialog = true
                    }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Compartir lista con...")
                    }

                }
            )
        },
        floatingActionButton = {
            if (categories.isNotEmpty()) {
                FloatingActionButton(onClick = {
                    showProductDialog = true
                    selectedCategoryId = categories.first().id
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                }
            }
        }
    ) { padding ->
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (categories.isEmpty()) "Crea una categoría (icono superior) para empezar."
                    else "No hay productos en esta lista.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp) // Espacio para que el FAB no tape el último producto
            ) {
                // Recorremos las categorías
                categories.forEach { category ->
                    // Filtramos los productos que pertenecen a esta categoría
                    val productsInCategory = products.filter { it.categoryId == category.id }

                    // Solo mostramos la cabecera si hay productos en esa categoría
                    if (productsInCategory.isNotEmpty()) {
                        stickyHeader {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = category.name,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Pintamos los productos de esta categoría
                        items(productsInCategory, key = { it.id }) { product ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = product.isCompleted,
                                    onCheckedChange = { viewModel.toggleProduct(product) }
                                )
                                Text(
                                    text = product.name,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = if (product.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (product.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                                )
                                // Botón para borrar el producto
                                IconButton(onClick = { viewModel.deleteProduct(product) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar producto",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }

    // --- DIÁLOGOS (Se quedan exactamente igual) ---
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Nueva Categoría") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Nombre (Ej: Lácteos)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        viewModel.addCategory(newCategoryName)
                        newCategoryName = ""
                        showCategoryDialog = false
                    }
                }) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showCategoryDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showProductDialog) {
        AlertDialog(
            onDismissRequest = { showProductDialog = false },
            title = { Text("Nuevo Producto") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newProductName,
                        onValueChange = { newProductName = it },
                        label = { Text("Nombre del producto") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box {
                        OutlinedButton(
                            onClick = { expandedDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val categoryName = categories.find { it.id == selectedCategoryId }?.name
                                ?: "Elegir Categoría"
                            Text(categoryName)
                        }
                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategoryId = category.id
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newProductName.isNotBlank() && selectedCategoryId.isNotBlank()) {
                        viewModel.addProduct(newProductName, selectedCategoryId)
                        newProductName = ""
                        showProductDialog = false
                    }
                }) { Text("Añadir") }
            },
            dismissButton = {
                TextButton(onClick = { showProductDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if(showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("Compartir Lista") },
            text = {
                Column {
                    Text("Introduce el correo de la persona con la que quieres compartir la lista:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailToShare,
                        onValueChange = { emailToShare = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true
                    )
                    // Mostrar mensaje de éxito o error
                    shareResult?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = msg,
                            color = if (msg.contains("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (emailToShare.isNotBlank()) {
                        viewModel.shareList(emailToShare)
                    }
                }) { Text("Compartir") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showShareDialog = false
                    viewModel.clearShareResult()
                    emailToShare = ""
                }) { Text("Cerrar") }
            }
        )
    }
}