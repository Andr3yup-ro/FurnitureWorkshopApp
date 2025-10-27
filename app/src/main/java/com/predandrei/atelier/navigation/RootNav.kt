package com.predandrei.atelier.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RootNav() {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("dashboard") { Text("Dashboard") }
        composable("projects") { Text("Projects") }
        composable("clients") { Text("Clients") }
        composable("inventory") { Text("Inventory") }
        composable("finance") { Text("Finance") }
        composable("payments") { Text("Payments") }
        composable("settings") { Text("Settings") }
    }
}
