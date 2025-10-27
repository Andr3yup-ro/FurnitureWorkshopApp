package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.ui.viewmodel.InventoryViewModel
import com.predandrei.atelier.ui.viewmodel.ProjectsViewModel
import com.predandrei.atelier.ui.viewmodel.FinanceViewModel
import com.predandrei.atelier.util.CurrencyRon

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val invVm: InventoryViewModel = hiltViewModel()
    val projVm: ProjectsViewModel = hiltViewModel()
    val finVm: FinanceViewModel = hiltViewModel()

    val items by invVm.items.collectAsState()
    val lowStock = items.count { it.quantity <= it.minStock }
    val projects by projVm.projects.collectAsState()
    val activeProjects = projects.count { it.status.name == "IN_PROGRESS" }
    val profit by finVm.profitRon.collectAsState()

    val cards = listOf(
        DashboardItem("Active projects", "$activeProjects in progress", Icons.Rounded.Work),
        DashboardItem("Inventory alerts", "$lowStock low stock", Icons.Rounded.Inventory2),
        DashboardItem("Profit", CurrencyRon.formatMinorUnits(profit), Icons.Rounded.CheckCircle),
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cards) { item ->
            ElevatedCard(onClick = { /* TODO: navigate */ }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private data class DashboardItem(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
