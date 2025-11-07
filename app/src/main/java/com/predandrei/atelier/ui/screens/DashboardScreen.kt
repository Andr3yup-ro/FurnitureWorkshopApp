package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.*
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
import com.predandrei.atelier.ui.viewmodel.PaymentsViewModel
import com.predandrei.atelier.util.CurrencyRon

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onOpenProjects: () -> Unit = {},
    onOpenInventory: () -> Unit = {},
    onOpenFinance: () -> Unit = {},
) {
    val invVm: InventoryViewModel = hiltViewModel()
    val projVm: ProjectsViewModel = hiltViewModel()
    val finVm: FinanceViewModel = hiltViewModel()
    val payVm: PaymentsViewModel = hiltViewModel()

    val items by invVm.items.collectAsState()
    val lowStock = items.count { it.quantity <= it.minStock }
    val projects by projVm.projects.collectAsState()
    val activeProjects = projects.count { it.status.name == "IN_PROGRESS" }
    val profit by finVm.profitRon.collectAsState()
    val txs by finVm.transactions.collectAsState()
    val installments by payVm.allInstallments.collectAsState()

    val today = java.time.LocalDate.now()
    val firstOfThisMonth = today.withDayOfMonth(1)
    val firstOfPrevMonth = firstOfThisMonth.minusMonths(1)
    val endOfPrevMonth = firstOfThisMonth.minusDays(1)
    val monthlyRevenue = txs.filter { it.type == com.predandrei.atelier.data.model.TransactionType.REVENUE && java.time.LocalDate.parse(it.date) >= firstOfThisMonth }
        .sumOf { it.amountRon }
    val prevMonthlyRevenue = txs.filter { it.type == com.predandrei.atelier.data.model.TransactionType.REVENUE && java.time.LocalDate.parse(it.date) in firstOfPrevMonth..endOfPrevMonth }
        .sumOf { it.amountRon }
    val deltaPct = if (prevMonthlyRevenue == 0L) 100 else (((monthlyRevenue - prevMonthlyRevenue).toDouble() / kotlin.math.max(1.0, prevMonthlyRevenue.toDouble())) * 100).toInt()
    val deltaLabel = (if (deltaPct >= 0) "+" else "") + "$deltaPct% from monthly"

    val pendingAmount = installments.filter { !it.paid }.sumOf { it.amountRon }
    val overdueCount = installments.count { !it.paid && java.time.LocalDate.parse(it.dueDate) < today }

    val cards = listOf(
        StatsItem(
            title = "Active Projects",
            primary = activeProjects.toString(),
            secondary = "In Progress",
            icon = Icons.Rounded.Work
        ),
        StatsItem(
            title = "Stock Items",
            primary = items.size.toString(),
            secondary = "$lowStock low stock",
            icon = Icons.Rounded.Inventory2
        ),
        StatsItem(
            title = "Monthly Revenue",
            primary = CurrencyRon.formatMinorUnits(monthlyRevenue),
            secondary = deltaLabel,
            icon = Icons.Rounded.CheckCircle
        ),
        StatsItem(
            title = "Pending Payments",
            primary = CurrencyRon.formatMinorUnits(pendingAmount),
            secondary = "$overdueCount overdue",
            icon = Icons.Rounded.CheckCircle
        ),
        StatsItem(
            title = "Profit",
            primary = CurrencyRon.formatMinorUnits(profit),
            secondary = "Total Profit",
            icon = Icons.Rounded.CheckCircle
        ),
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Management Panel", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp)) }
        items(cards) { item ->
            val onClick = when (item.title) {
                "Active Projects" -> onOpenProjects
                "Stock Items" -> onOpenInventory
                "Profit" -> onOpenFinance
                else -> ({})
            }
            ElevatedCard(onClick = onClick) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(text = item.primary, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                        if (item.secondary.isNotBlank()) {
                            Text(text = item.secondary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

private data class StatsItem(
    val title: String,
    val primary: String,
    val secondary: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
