package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.Project
import com.predandrei.atelier.data.model.ProjectStatus
import com.predandrei.atelier.ui.viewmodel.ProjectsViewModel
import com.predandrei.atelier.ui.viewmodel.ClientsViewModel
import com.predandrei.atelier.ui.viewmodel.FinanceViewModel
import com.predandrei.atelier.util.CurrencyRon

@Composable
fun ProjectsScreen(
    modifier: Modifier = Modifier,
    onEdit: (Long?) -> Unit = {},
    vm: ProjectsViewModel = hiltViewModel()
) {
    val projects by vm.projects.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(modifier.fillMaxSize()) {
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Search projects") }, modifier = Modifier.padding(16.dp))
        LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filtered = projects.filter { it.title.contains(query, ignoreCase = true) || it.description.orEmpty().contains(query, true) }
            items(filtered, key = { it.id }) { p ->
                ProjectRowRich(p, onClick = { onEdit(p.id) })
            }
        }
    }
}

@Composable
private fun ProjectRowRich(p: Project, onClick: () -> Unit, clientsVm: ClientsViewModel = hiltViewModel(), finVm: FinanceViewModel = hiltViewModel()) {
    val clients by clientsVm.clients.collectAsState()
    val clientName = clients.firstOrNull { it.id == p.clientId }?.name ?: "—"
    val txs by finVm.transactions.collectAsState()
    val usages by finVm.materialUsages.collectAsState()
    val inventory by finVm.inventory.collectAsState()
    val labor by finVm.laborEntries.collectAsState()

    val paid = remember(txs) { txs.filter { it.projectId == p.id && it.type == com.predandrei.atelier.data.model.TransactionType.REVENUE }.sumOf { it.amountRon } }
    val overhead = remember(txs) { txs.filter { it.projectId == p.id && it.type == com.predandrei.atelier.data.model.TransactionType.EXPENSE && it.category.equals("OVERHEAD", true) }.sumOf { it.amountRon } }
    val matMap = remember(inventory) { inventory.associateBy { it.id } }
    val projUsages = remember(usages) { usages.filter { it.projectId == p.id } }
    val materialsCost = remember(projUsages, inventory) { projUsages.sumOf { (matMap[it.inventoryItemId]?.priceRon ?: 0L) * it.quantityUsed } }
    val laborCost = remember(labor) { labor.filter { it.projectId == p.id }.sumOf { (it.hourlyRateRon * it.minutes) / 60 } }
    val profit = p.valueRon - (materialsCost + laborCost + overhead)
    val remaining = (p.valueRon - paid).coerceAtLeast(0)
    val progress = if (p.valueRon == 0L) 0f else (paid.toFloat() / p.valueRon.toFloat()).coerceIn(0f, 1f)

    ElevatedCard(modifier = Modifier.clickable { onClick() }) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: Title + Total on the right
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column {
                        Text(p.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(clientName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            p.deadline?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
                Text(CurrencyRon.formatMinorUnits(p.valueRon), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            if (!p.description.isNullOrBlank()) Text(p.description!!, style = MaterialTheme.typography.bodySmall)

            // Status chip
            val (statusLabel, statusColor) = when (p.status) {
                ProjectStatus.IN_PROGRESS -> "In Progress" to MaterialTheme.colorScheme.primary
                ProjectStatus.COMPLETED -> "Completed" to MaterialTheme.colorScheme.secondary
                ProjectStatus.ON_HOLD -> "On Hold" to MaterialTheme.colorScheme.tertiary
                else -> "Pending" to MaterialTheme.colorScheme.onSurfaceVariant
            }
            AssistChip(onClick = { }, label = { Text(statusLabel) }, colors = AssistChipDefaults.assistChipColors(labelColor = statusColor))

            // Financial breakdown grid-like rows
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) { Text("Materials", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(CurrencyRon.formatMinorUnits(materialsCost), fontWeight = FontWeight.SemiBold) }
                Column(Modifier.weight(1f)) { Text("Labor", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(CurrencyRon.formatMinorUnits(laborCost), fontWeight = FontWeight.SemiBold) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) { Text("Overhead", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(CurrencyRon.formatMinorUnits(overhead), fontWeight = FontWeight.SemiBold) }
                Column(Modifier.weight(1f)) { Text("Profit", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(CurrencyRon.formatMinorUnits(profit), fontWeight = FontWeight.SemiBold) }
            }

            Text("Paid: ${CurrencyRon.formatMinorUnits(paid)}    Remaining: ${CurrencyRon.formatMinorUnits(remaining)}", style = MaterialTheme.typography.bodyMedium)

            // Progress
            LinearProgressIndicator(progress = progress)
            Text("Total: ${CurrencyRon.formatMinorUnits(p.valueRon)}    ${(progress * 100).toInt()}% Completed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Allocated Stock
            if (projUsages.isNotEmpty()) {
                HorizontalDivider()
                Text("Allocated Stock", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    projUsages.forEach { u ->
                        val item = matMap[u.inventoryItemId]
                        val unit = item?.priceRon ?: 0L
                        val total = unit * u.quantityUsed
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item?.name ?: "Item #${u.inventoryItemId}")
                            Text("Qty: ${u.quantityUsed}  Unit: ${CurrencyRon.formatMinorUnits(unit)}  Total: ${CurrencyRon.formatMinorUnits(total)}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Labor Entries (summary per entry)
            val projLabor = labor.filter { it.projectId == p.id }
            if (projLabor.isNotEmpty()) {
                HorizontalDivider()
                Text("Labor Entries", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    projLabor.forEach { le ->
                        val hours = le.minutes / 60
                        val total = (le.hourlyRateRon * le.minutes) / 60
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(le.date)
                            Text("${hours}h  ${CurrencyRon.formatMinorUnits(le.hourlyRateRon)}/h  Total: ${CurrencyRon.formatMinorUnits(total)}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
