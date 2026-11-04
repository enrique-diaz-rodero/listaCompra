package com.enriquediaz.listacompra.ui.lists

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.enriquediaz.listacompra.data.repository.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    currentPrefs: UserPreferences,
    onDismiss: () -> Unit,
    onSave: (String, Boolean, Boolean) -> Unit
) {
    // Inicializamos los estados con las preferencias actuales
    var supermarket by remember { mutableStateOf(currentPrefs.supermarket) }
    var showImages by remember { mutableStateOf(currentPrefs.showImages) }
    var showPrices by remember { mutableStateOf(currentPrefs.showPrices) }
    var expanded by remember { mutableStateOf(false) }

    val supermarkets = listOf("Mercadona", "Carrefour", "Consum", "Dia", "Alcampo", "Otro / Ninguno")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustes de la App") },
        text = {
            Column {
                Text("Personaliza tu experiencia de compra:")
                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Súper
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (supermarket.isEmpty()) "Ninguno seleccionado" else supermarket)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        supermarkets.forEach { store ->
                            DropdownMenuItem(
                                text = { Text(store) },
                                onClick = {
                                    supermarket = if (store == "Otro / Ninguno") "" else store
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggles
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = showImages, onCheckedChange = { showImages = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mostrar imágenes de productos")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = showPrices, onCheckedChange = { showPrices = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mostrar precios estimados")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(supermarket, showImages, showPrices)
                onDismiss()
            }) {
                Text("Guardar cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}