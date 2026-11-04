package com.enriquediaz.listacompra.ui.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
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
import coil.compose.AsyncImage
import com.enriquediaz.listacompra.utils.AutoCategorizer
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListDetailScreen(
    navController: NavController,
    viewModel: ListDetailViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()

    // Estados para miembros
    val members by viewModel.membersDetails.collectAsState()
    val listDetails by viewModel.listDetails.collectAsState()
    val currentUserId = viewModel.getCurrentUserId()
    var showMembersDialog by remember { mutableStateOf(false) }

    var showCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    var showProductDialog by remember { mutableStateOf(false) }
    var newProductName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    // NUEVO: Estado para guardar el EAN seleccionado
    var selectedEan by remember { mutableStateOf<String?>(null) }

    var showShareDialog by remember { mutableStateOf(false) }
    var emailToShare by remember { mutableStateOf("") }
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
                    IconButton(onClick = { showMembersDialog = true }) {
                        Icon(Icons.Default.Group, contentDescription = "Miembros de la lista")
                    }
                    IconButton(onClick = { showCategoryDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Nueva Categoría")
                    }
                    IconButton(onClick = { showShareDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Compartir")
                    }
                }
            )
        },
        floatingActionButton = {
            if (categories.isNotEmpty()) {
                FloatingActionButton(onClick = {
                    showProductDialog = true
                    selectedCategoryId = categories.first().id
                    selectedEan = null // Limpiamos el EAN al abrir
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                }
            }
        }
    ) { padding ->
        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
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
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                categories.forEach { category ->
                    val productsInCategory = products.filter { it.categoryId == category.id }
                    if (productsInCategory.isNotEmpty()) {
                        stickyHeader {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = category.name,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        items(productsInCategory, key = { it.id }) { product ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = product.isCompleted,
                                    onCheckedChange = { viewModel.toggleProduct(product) }
                                )

                                // NUEVO: Columna para el nombre y el precio
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = if (product.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (product.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                                    )
                                    // Mostramos el precio si existe
                                    product.price?.let { price ->
                                        Text(
                                            text = "${String.format("%.2f", price)} €",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

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

    // --- DIÁLOGO: GESTIONAR MIEMBROS ---
    if (showMembersDialog) {
        AlertDialog(
            onDismissRequest = { showMembersDialog = false },
            title = { Text("Miembros de la lista") },
            text = {
                Column {
                    if (members.isEmpty()) {
                        Text("Cargando miembros...")
                    } else {
                        members.forEach { member ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (member.uid == currentUserId) "${member.email} (Tú)" else member.email,
                                    modifier = Modifier.weight(1f)
                                )
                                if (member.uid != currentUserId && member.uid != listDetails?.ownerId) {
                                    IconButton(onClick = { viewModel.removeMember(member.uid) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar miembro",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMembersDialog = false }) { Text("Cerrar") }
            }
        )
    }

    // --- DIÁLOGO: NUEVA CATEGORÍA ---
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

    // --- DIÁLOGO: NUEVO PRODUCTO (CON BUSCADOR Y PRECIOS) ---
    if (showProductDialog) {
        val searchResults by viewModel.searchResults.collectAsState()
        val currentPrice by viewModel.currentPrice.collectAsState()
        // Leemos las preferencias para saber si mostrar imágenes
        val prefs by viewModel.userPreferences.collectAsState(initial = null)

        // DEBOUNCE: Esperar a que deje de escribir
        LaunchedEffect(newProductName) {
            if (newProductName.length > 2 && selectedEan == null) {
                // Solo busca en la API si no hemos seleccionado ya un producto de la lista
                delay(500)
                viewModel.searchApi(newProductName)
            } else if (newProductName.length <= 2) {
                viewModel.clearSearchResults()
            }
        }

        AlertDialog(
            onDismissRequest = {
                showProductDialog = false
                viewModel.clearSearchResults()
                viewModel.clearPrice()
            },
            title = { Text("Nuevo Producto") },
            text = {
                Column(modifier = Modifier.heightIn(max = 450.dp)) {
                    OutlinedTextField(
                        value = newProductName,
                        onValueChange = { nuevoTexto ->
                            newProductName = nuevoTexto
                            selectedEan = null // Si escribe, pierde la selección previa
                            viewModel.clearPrice() // Limpiamos el precio anterior

                            // AUTO-CLASIFICACIÓN LOCAL
                            val categoriaSugerida = AutoCategorizer.sugerirCategoria(nuevoTexto)
                            val categoriaReal = categories.find { it.name == categoriaSugerida }
                            if (categoriaReal != null) {
                                selectedCategoryId = categoriaReal.id
                            }
                        },
                        label = { Text("Nombre del producto") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // LISTA DE SUGERENCIAS CON IMÁGENES
                    if (searchResults.isNotEmpty()) {
                        Text("Sugerencias:", style = MaterialTheme.typography.bodySmall)
                        LazyColumn {
                            items(searchResults) { producto ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            newProductName = producto.name ?: ""
                                            selectedEan = producto.code // Guardamos el EAN
                                            viewModel.clearSearchResults()

                                            // Re-evaluamos la categoría con el nombre seleccionado
                                            val catSug = AutoCategorizer.sugerirCategoria(newProductName)
                                            val catReal = categories.find { it.name == catSug }
                                            if (catReal != null) selectedCategoryId = catReal.id

                                            // NUEVO: Buscar el precio
                                            viewModel.fetchPrice(producto.code)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Solo mostramos la imagen si el usuario lo tiene activado en preferencias
                                    if (prefs?.showImages == true && producto.imageUrl != null) {
                                        AsyncImage(
                                            model = producto.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    } else {
                                        Box(modifier = Modifier.size(40.dp).background(Color.LightGray))
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    Text(text = producto.name ?: "Sin nombre", style = MaterialTheme.typography.bodyMedium)
                                }
                                HorizontalDivider()
                            }
                        }
                    }

                    // NUEVO: Mostrar el precio si se ha encontrado
                    currentPrice?.let { precio ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Precio estimado: ${String.format("%.2f", precio)} €",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // DESPLEGABLE DE CATEGORÍAS
                    Box {
                        OutlinedButton(
                            onClick = { expandedDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val categoryName = categories.find { it.id == selectedCategoryId }?.name ?: "Elegir Categoría"
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
                        // NUEVO: Pasamos el EAN y el precio al ViewModel
                        viewModel.addProduct(newProductName, selectedCategoryId, selectedEan, currentPrice)
                        newProductName = ""
                        selectedEan = null
                        showProductDialog = false
                        viewModel.clearSearchResults()
                        viewModel.clearPrice()
                    }
                }) { Text("Añadir") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showProductDialog = false
                    viewModel.clearSearchResults()
                    viewModel.clearPrice()
                }) { Text("Cancelar") }
            }
        )
    }

    // --- DIÁLOGO: COMPARTIR LISTA ---
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
                    shareResult?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = msg,
                            color = if (msg.contains("Error") || msg.contains("No existe")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
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