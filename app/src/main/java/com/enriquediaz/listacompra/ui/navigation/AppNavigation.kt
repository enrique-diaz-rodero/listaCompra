package com.enriquediaz.listacompra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.enriquediaz.listacompra.ui.auth.LoginScreen
import com.enriquediaz.listacompra.ui.lists.DashboardScreen
import com.enriquediaz.listacompra.ui.detail.ListDetailScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 1. Comprobamos si Firebase tiene un usuario guardado en la memoria local
    val auth = FirebaseAuth.getInstance()

    // 2. Si currentUser NO es nulo, la app arrancará directamente en el Dashboard.
    // Si es nulo, arrancará en el Login.
    val startDestination = if (auth.currentUser != null) "dashboard" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("dashboard") {
            DashboardScreen(navController = navController)
        }

        composable(
            route = "list_detail/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.StringType })
        ) {
            ListDetailScreen(navController = navController)
        }
    }
}