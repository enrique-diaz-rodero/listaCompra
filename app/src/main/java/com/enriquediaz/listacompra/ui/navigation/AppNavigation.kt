package com.enriquediaz.listacompra.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.enriquediaz.listacompra.ui.auth.LoginScreen
import com.enriquediaz.listacompra.ui.lists.DashboardScreen
import com.enriquediaz.listacompra.ui.detail.ListDetailScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login"){

        composable("login"){
            LoginScreen(navController = navController)
        }

        composable("dashboard"){
            DashboardScreen(navController = navController)
        }

        // Esta ruta recibe un argumento: el ID de la lista
        composable("list_detail/{listId}"){ backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            ListDetailScreen(listId = listId, navController = navController)

        }
    }
}