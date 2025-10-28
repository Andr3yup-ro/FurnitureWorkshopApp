package com.predandrei.atelier.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.predandrei.atelier.ui.screens.DashboardScreen
import com.predandrei.atelier.ui.screens.ProjectsScreen
import com.predandrei.atelier.ui.screens.ProjectEditScreen
import com.predandrei.atelier.ui.screens.ClientsScreen
import com.predandrei.atelier.ui.screens.ClientEditScreen
import com.predandrei.atelier.ui.screens.InventoryScreen
import com.predandrei.atelier.ui.screens.InventoryEditScreen
import com.predandrei.atelier.ui.screens.FinanceScreen
import com.predandrei.atelier.ui.screens.ProjectPaymentsScreen
import com.predandrei.atelier.ui.screens.ProjectMaterialsScreen
import com.predandrei.atelier.ui.screens.SettingsScreen
import com.predandrei.atelier.ui.screens.CategoriesScreen
import com.predandrei.atelier.ui.screens.CategoryEditScreen
import com.predandrei.atelier.ui.screens.SuppliersScreen
import com.predandrei.atelier.ui.screens.SupplierEditScreen
import androidx.compose.material.icons.rounded.Add
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import com.predandrei.atelier.R

private data class Destination(
    val route: String,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomDestinations = listOf(
    Destination("dashboard", R.string.nav_dashboard, Icons.Rounded.Dashboard),
    Destination("projects", R.string.nav_projects, Icons.Rounded.Work),
    Destination("inventory", R.string.nav_inventory, Icons.Rounded.Inventory2),
    Destination("clients", R.string.nav_clients, Icons.Rounded.Person),
    Destination("settings", R.string.nav_settings, Icons.Rounded.Settings),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNav() {
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route ?: bottomDestinations.first().route

    val isBottom = bottomDestinations.any { it.route == currentRoute }
    Scaffold(
        topBar = { TopAppBar(
            title = {
                val titleRes = bottomDestinations.firstOrNull { it.route == currentRoute }?.labelRes
                if (titleRes != null) Text(stringResource(titleRes)) else Text(currentRoute.replaceFirstChar { it.uppercase() })
            },
            navigationIcon = {
                if (!isBottom) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.clickable { navController.navigateUp() }
                    )
                }
            }
        ) },
        bottomBar = {
            NavigationBar {
                bottomDestinations.forEach { dest ->
                    val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = stringResource(dest.labelRes)) },
                        label = { Text(stringResource(dest.labelRes)) }
                    )
                }
            }
        },
        floatingActionButton = {
            val ctx = LocalContext.current
            when (currentRoute) {
                "projects" -> FloatingActionButton(onClick = { navController.navigate("project_edit") }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add project")
                }
                "clients" -> FloatingActionButton(onClick = { navController.navigate("client_edit") }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add client")
                }
                "inventory" -> FloatingActionButton(onClick = { navController.navigate("inventory_edit") }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add item")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            route = "root"
        ) {
            composable("dashboard") {
                DashboardScreen(
                    modifier = Modifier.fillMaxSize(),
                    onOpenProjects = { navController.navigate("projects") },
                    onOpenInventory = { navController.navigate("inventory") },
                    onOpenFinance = { navController.navigate("finance") }
                )
            }
            composable("projects") {
                ProjectsScreen(modifier = Modifier.fillMaxSize(), onEdit = { id ->
                    navController.navigate("project_edit" + (id?.let { "/$it" } ?: ""))
                })
            }
            composable("clients") {
                ClientsScreen(modifier = Modifier.fillMaxSize(), onEdit = { id ->
                    navController.navigate("client_edit" + (id?.let { "/$it" } ?: ""))
                })
            }
            composable("inventory") {
                InventoryScreen(modifier = Modifier.fillMaxSize(), onEdit = { id ->
                    navController.navigate("inventory_edit" + (id?.let { "/$it" } ?: ""))
                })
            }
            composable("finance") { FinanceScreen(modifier = Modifier.fillMaxSize()) }
            composable("project_payments/{projectId}") { backStack ->
                val id = backStack.arguments?.getString("projectId")?.toLongOrNull() ?: 0L
                ProjectPaymentsScreen(projectId = id, onDone = { navController.popBackStack() })
            }
            composable("project_materials/{projectId}") { backStack ->
                val id = backStack.arguments?.getString("projectId")?.toLongOrNull() ?: 0L
                ProjectMaterialsScreen(projectId = id, onDone = { navController.popBackStack() })
            }
            composable("settings") { SettingsScreen(
                onOpenCategories = { navController.navigate("categories") },
                onOpenSuppliers = { navController.navigate("suppliers") }
            ) }
            composable("categories") { CategoriesScreen(onEdit = { id -> navController.navigate("category_edit" + (id?.let { "/$it" } ?: "")) }) }
            composable("category_edit") { CategoryEditScreen(categoryId = null, onSaved = { navController.popBackStack() }) }
            composable("category_edit/{id}") { backStack ->
                val id = backStack.arguments?.getString("id")?.toLongOrNull()
                CategoryEditScreen(categoryId = id, onSaved = { navController.popBackStack() })
            }
            composable("suppliers") { SuppliersScreen(onEdit = { id -> navController.navigate("supplier_edit" + (id?.let { "/$it" } ?: "")) }) }
            composable("supplier_edit") { SupplierEditScreen(supplierId = null, onSaved = { navController.popBackStack() }) }
            composable("supplier_edit/{id}") { backStack ->
                val id = backStack.arguments?.getString("id")?.toLongOrNull()
                SupplierEditScreen(supplierId = id, onSaved = { navController.popBackStack() })
            }

            // Edit/Create routes (optional id)
            composable("project_edit") { ProjectEditScreen(projectId = null, onSaved = { navController.popBackStack() }) }
            composable("project_edit/{id}") { backStack ->
                val id = backStack.arguments?.getString("id")?.toLongOrNull()
                ProjectEditScreen(projectId = id, onSaved = { navController.popBackStack() }, onManagePayments = { pid ->
                    navController.navigate("project_payments/$pid")
                }, onManageMaterials = { pid -> navController.navigate("project_materials/$pid") })
            }
            composable("client_edit") { ClientEditScreen(clientId = null, onSaved = { navController.popBackStack() }) }
            composable("client_edit/{id}") { backStack ->
                val id = backStack.arguments?.getString("id")?.toLongOrNull()
                ClientEditScreen(clientId = id, onSaved = { navController.popBackStack() })
            }
            composable("inventory_edit") { InventoryEditScreen(itemId = null, onSaved = { navController.popBackStack() }) }
            composable("inventory_edit/{id}") { backStack ->
                val id = backStack.arguments?.getString("id")?.toLongOrNull()
                InventoryEditScreen(itemId = id, onSaved = { navController.popBackStack() })
            }
        }
    }
}
