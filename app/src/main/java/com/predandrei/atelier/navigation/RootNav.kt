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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Download
import androidx.compose.runtime.*
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
import com.predandrei.atelier.ui.screens.ReportsScreen
import com.predandrei.atelier.ui.screens.PaymentsScreen
import com.predandrei.atelier.ui.screens.CategoriesScreen
import com.predandrei.atelier.ui.screens.CategoryEditScreen
import com.predandrei.atelier.ui.screens.SuppliersScreen
import com.predandrei.atelier.ui.screens.SupplierEditScreen
import com.predandrei.atelier.ui.screens.ProjectLaborScreen
import androidx.compose.material.icons.rounded.Add
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import com.predandrei.atelier.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.ui.viewmodel.ReportsViewModel

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
    val reportsVm: ReportsViewModel = hiltViewModel()
    val ctx = LocalContext.current
    var pendingProjectId by remember { mutableStateOf<Long?>(null) }
    val createDocLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) {
            when (currentRoute) {
                "projects" -> reportsVm.exportProjects(uri, ctx.contentResolver)
                "inventory" -> reportsVm.exportInventory(uri, ctx.contentResolver)
                "finance" -> reportsVm.exportFinance(uri, ctx.contentResolver)
                "project_edit/{id}", "project_payments/{projectId}", "project_materials/{projectId}", "project_labor/{projectId}" -> {
                    pendingProjectId?.let { reportsVm.exportProject(uri, ctx.contentResolver, it) }
                }
            }
            pendingProjectId = null
        }
    }
    Scaffold(
        topBar = { androidx.compose.foundation.layout.Column {
            CenterAlignedTopAppBar(
            title = {
                val topRoutes = listOf("projects", "inventory", "finance", "payments")
                if (currentRoute in topRoutes) {
                    Text("Management Panel")
                } else {
                    val titleRes = bottomDestinations.firstOrNull { it.route == currentRoute }?.labelRes
                    if (titleRes != null) Text(stringResource(titleRes)) else Text(currentRoute.replaceFirstChar { it.uppercase() })
                }
            },
            navigationIcon = {
                if (!isBottom) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.clickable { navController.navigateUp() }
                    )
                }
            },
            actions = {
                if (currentRoute in listOf("projects", "inventory", "finance", "project_edit/{id}", "project_payments/{projectId}", "project_materials/{projectId}", "project_labor/{projectId}")) {
                    IconButton(onClick = {
                        val defaultName = when (currentRoute) {
                            "projects" -> "projects-report.pdf"
                            "inventory" -> "inventory-report.pdf"
                            "finance" -> "finance-report.pdf"
                            "project_edit/{id}", "project_payments/{projectId}", "project_materials/{projectId}", "project_labor/{projectId}" -> {
                                val id = when (currentRoute) {
                                    "project_edit/{id}" -> backStackEntry?.arguments?.getString("id")?.toLongOrNull()
                                    else -> backStackEntry?.arguments?.getString("projectId")?.toLongOrNull()
                                }
                                pendingProjectId = id
                                "project-${id ?: "unknown"}-report.pdf"
                            }
                            else -> "report.pdf"
                        }
                        createDocLauncher.launch(defaultName)
                    }) {
                        Icon(Icons.Rounded.Download, contentDescription = "Export PDF")
                    }
                }
            }
            )
            // Top tabs to match website
            val tabs = listOf(
                Destination("projects", R.string.nav_projects, Icons.Rounded.Work),
                Destination("inventory", R.string.nav_inventory, Icons.Rounded.Inventory2),
                Destination("finance", R.string.nav_finance, Icons.Rounded.Dashboard),
                Destination("payments", R.string.nav_payments, Icons.Rounded.Dashboard),
            )
            val selectedIndex = tabs.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
            if (currentRoute in tabs.map { it.route }) {
                TabRow(selectedTabIndex = selectedIndex) {
                    tabs.forEachIndexed { index, dest ->
                        Tab(
                            selected = index == selectedIndex,
                            onClick = {
                                if (currentRoute != dest.route) {
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            text = { Text(stringResource(dest.labelRes)) }
                        )
                    }
                }
            }
        } },
        bottomBar = {},
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
            startDestination = "projects",
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
            composable("payments") { PaymentsScreen() }
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
                onOpenSuppliers = { navController.navigate("suppliers") },
                onOpenReports = { navController.navigate("reports") }
            ) }
            composable("reports") { ReportsScreen() }
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
                }, onManageMaterials = { pid -> navController.navigate("project_materials/$pid") }, onManageLabor = { pid -> navController.navigate("project_labor/$pid") })
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
            composable("project_labor/{projectId}") { backStack ->
                val id = backStack.arguments?.getString("projectId")?.toLongOrNull() ?: 0L
                ProjectLaborScreen(projectId = id, onDone = { navController.popBackStack() })
            }
        }
    }
}
