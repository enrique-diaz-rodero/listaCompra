package com.enriquediaz.listacompra.data.model

import com.google.firebase.firestore.DocumentId

data class ShoppingList(
    @DocumentId  val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val members: List<String> = emptyList() // Emails o UIDs con acceso //
)
