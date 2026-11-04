package com.enriquediaz.listacompra.data.model

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val categoryId: String = "",
    val isCompleted: Boolean = false,
    val tags: List<String> = emptyList(),
    val ean: String? = null,   // <-- NUEVO: Código de barras
    val price: Double? = null  // <-- NUEVO: Precio guardado
)
