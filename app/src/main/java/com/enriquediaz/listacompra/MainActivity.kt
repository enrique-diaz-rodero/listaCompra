package com.enriquediaz.listacompra

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.enriquediaz.listacompra.ui.navigation.AppNavigation
import com.enriquediaz.listacompra.ui.theme.ListaCompraTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // =========================================================
        // INICIO DEL DIAGNÓSTICO DE SESIÓN
        // =========================================================
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        Log.d("FirebaseDebug", "currentUser al arrancar: ${user?.uid ?: "null"}")

        if (user != null) {
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseDebug", "Recarga exitosa, sesión válida para UID: ${user.uid}")
                } else {
                    Log.e("FirebaseDebug", "Error al recargar sesión: ${task.exception?.message}")
                }
            }
        } else {
            Log.e("FirebaseDebug", "No hay ningún usuario en la caché local de Firebase Auth.")
        }
        // =========================================================
        // FIN DEL DIAGNÓSTICO
        // =========================================================

        setContent {
            ListaCompraTheme {
                AppNavigation()
            }
        }
    }
}
