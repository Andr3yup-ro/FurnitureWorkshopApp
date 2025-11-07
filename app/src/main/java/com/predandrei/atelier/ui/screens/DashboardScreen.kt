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
        DashboardItem("Active projects", "$activeProjects in progress", Icons.Rounded.Work),
        DashboardItem("Stock Items", "${items.size} total", Icons.Rounded.Inventory2),
        DashboardItem("Low stock", "$lowStock items", Icons.Rounded.Inventory2),
        DashboardItem("Monthly Revenue", "${CurrencyRon.formatMinorUnits(monthlyRevenue)}  $deltaLabel", Icons.Rounded.CheckCircle),
        DashboardItem("Pending Payments", "${CurrencyRon.formatMinorUnits(pendingAmount)}  $overdueCount overdue", Icons.Rounded.CheckCircle),
        DashboardItem("Profit", CurrencyRon.formatMinorUnits(profit), Icons.Rounded.CheckCircle),
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cards) { item ->
            val onClick = when (item.title) {
                "Active projects" -> onOpenProjects
                "Inventory alerts" -> onOpenInventory
                "Stock Items" -> onOpenInventory
                "Low stock" -> onOpenInventory
                "Profit" -> onOpenFinance
                else -> ({})
            }
            ElevatedCard(onClick = onClick) {
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
