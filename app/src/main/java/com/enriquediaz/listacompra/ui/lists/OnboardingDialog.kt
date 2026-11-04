package com.enriquediaz.listacompra.ui.lists

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingDialog(
    onSave: (String, Boolean, Boolean) -> Unit
) {
    var supermarket by remember { mutableStateOf("") }
    var showImages by remember { mutableStateOf(true) }
    var showPrices by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    val supermarkets = listOf("Mercadona", "Carrefour", "Consum", "Dia", "Alcampo", "Otro / Ninguno")

    AlertDialog(
        onDismissRequest = { /* No permitir cerrar sin guardar */ },
        title = { Text("¡Bienvenido a tu Lista!") },
        text = {
            Column {
                Text("Para darte la mejor experiencia, cuéntanos un poco sobre ti:")
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Supermercado
                Text("¿En qué supermercado sueles comprar?", style = MaterialTheme.typography.bodyMedium)
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (supermarket.isEmpty()) "Selecciona tu súper" else supermarket)
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

                // 2. Imágenes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = showImages, onCheckedChange = { showImages = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("¿Quieres ver imágenes de los productos?")
                }
                Spacer(modifier = Modifier.height(8.dp))

                // 3. Precios
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = showPrices, onCheckedChange = { showPrices = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("¿Quieres ver los precios estimados?")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(supermarket, showImages, showPrices) }) {
                Text("Empezar a usar la app")
            }
        }
    )
}
