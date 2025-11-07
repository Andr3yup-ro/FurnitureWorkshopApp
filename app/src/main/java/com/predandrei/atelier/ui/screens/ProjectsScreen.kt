package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.Project
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
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Rounded.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(p.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(clientName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!p.description.isNullOrBlank()) Text(p.description!!, style = MaterialTheme.typography.bodySmall)
            Text(p.status.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(8.dp))
            Text("Materials: ${CurrencyRon.formatMinorUnits(materialsCost)}")
            Text("Labor: ${CurrencyRon.formatMinorUnits(laborCost)}")
            Text("Overhead: ${CurrencyRon.formatMinorUnits(overhead)}")
            Text("Profit: ${CurrencyRon.formatMinorUnits(profit)}")
            Text("Paid: ${CurrencyRon.formatMinorUnits(paid)}    Remaining: ${CurrencyRon.formatMinorUnits(remaining)}")

            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(progress = progress)
            Text("${(progress * 100).toInt()}% Completed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (projUsages.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text("Allocated Stock", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                projUsages.forEach { u ->
                    val item = matMap[u.inventoryItemId]
                    val unit = item?.priceRon ?: 0L
                    val total = unit * u.quantityUsed
                    Text("${item?.name ?: "Item #${u.inventoryItemId}"}  •  Qty: ${u.quantityUsed}  •  Unit: ${CurrencyRon.formatMinorUnits(unit)}  •  Total: ${CurrencyRon.formatMinorUnits(total)}",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
