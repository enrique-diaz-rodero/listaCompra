package com.enriquediaz.listacompra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.enriquediaz.listacompra.ui.theme.ListaCompraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListaCompraTheme {
                // Aquí irá nuestra navegación, por ahora lo dejo marcado
                // AppNavigation()
            }
        }
    }
}


