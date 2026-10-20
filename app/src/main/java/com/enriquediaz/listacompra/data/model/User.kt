package com.enriquediaz.listacompra.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val uid: String = "",
    val email: String = "",
    val name: String = ""
)
