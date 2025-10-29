package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import com.predandrei.atelier.data.model.FinancialTransaction
import com.predandrei.atelier.data.model.TransactionType
import com.predandrei.atelier.ui.viewmodel.FinanceViewModel
import com.predandrei.atelier.ui.viewmodel.ProjectsViewModel
import com.predandrei.atelier.util.CurrencyRon
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(modifier: Modifier = Modifier, vm: FinanceViewModel = hiltViewModel()) {
    val txs by vm.transactions.collectAsState()
    val revenue by vm.revenueTotalRon.collectAsState()
    val expenses by vm.expenseTotalRon.collectAsState()
    val profit by vm.profitRon.collectAsState()

    val projectsVm: ProjectsViewModel = hiltViewModel()
    val projects by projectsVm.projects.collectAsState()

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(id = com.predandrei.atelier.R.string.revenue_fmt, CurrencyRon.formatMinorUnits(revenue)))
        Text(stringResource(id = com.predandrei.atelier.R.string.expenses_fmt, CurrencyRon.formatMinorUnits(expenses)))
        Text(stringResource(id = com.predandrei.atelier.R.string.profit_fmt, CurrencyRon.formatMinorUnits(profit)), color = MaterialTheme.colorScheme.primary)

        Spacer(Modifier.height(12.dp))
        Divider()
        Spacer(Modifier.height(12.dp))

        var type by remember { mutableStateOf(TransactionType.REVENUE) }
        var category by remember { mutableStateOf("") }
        var amountText by remember { mutableStateOf("") }

        // Filters
        var selectedProjectId by remember { mutableStateOf<Long?>(null) }
        var startDate by remember { mutableStateOf("") }
        var endDate by remember { mutableStateOf("") }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Type toggle
            Button(onClick = { type = if (type == TransactionType.REVENUE) TransactionType.EXPENSE else TransactionType.REVENUE }) {
                Text(if (type == TransactionType.REVENUE) stringResource(id = com.predandrei.atelier.R.string.revenue) else stringResource(id = com.predandrei.atelier.R.string.expense))
            }
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.category)) })
            OutlinedTextField(value = amountText, onValueChange = { amountText = amountText.replace(',', '.').filter { it.isDigit() || it == '.' }.let { t ->
                val first = t.indexOf('.')
                if (first == -1) t else t.substring(0, first + 1) + t.substring(first + 1).replace(".", "")
            } }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.amount_ron)) })
            Button(onClick = {
                val amt = com.predandrei.atelier.util.MoneyParser.toMinorUnits(amountText)
                if (amt > 0) vm.save(
                    FinancialTransaction(projectId = selectedProjectId, type = type, category = category.ifBlank { if (type == TransactionType.REVENUE) "REVENUE" else "EXPENSE" }, amountRon = amt, date = java.time.LocalDate.now().toString())
                )
                amountText = ""; category = ""
            }) { Text(stringResource(id = com.predandrei.atelier.R.string.add)) }
        }

        Spacer(Modifier.height(12.dp))
        // Filter row
        var projExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = projExpanded, onExpandedChange = { projExpanded = !projExpanded }) {
            OutlinedTextField(
                readOnly = true,
                value = selectedProjectId?.let { id -> projects.firstOrNull { it.id == id }?.title } ?: stringResource(id = com.predandrei.atelier.R.string.all_projects),
                onValueChange = {},
                label = { Text(stringResource(id = com.predandrei.atelier.R.string.project_filter)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projExpanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = projExpanded, onDismissRequest = { projExpanded = false }) {
                DropdownMenuItem(text = { Text(stringResource(id = com.predandrei.atelier.R.string.all_projects)) }, onClick = { selectedProjectId = null; projExpanded = false })
                projects.forEach { p -> DropdownMenuItem(text = { Text(p.title) }, onClick = { selectedProjectId = p.id; projExpanded = false }) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.start_date)) })
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.end_date)) })
        }

        val filtered = remember(txs, selectedProjectId, startDate, endDate, category) {
            txs.filter { t ->
                (selectedProjectId == null || t.projectId == selectedProjectId) &&
                (category.isBlank() || t.category.contains(category, ignoreCase = true)) &&
                (startDate.isBlank() || t.date >= startDate) &&
                (endDate.isBlank() || t.date <= endDate)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(stringResource(id = com.predandrei.atelier.R.string.overview_6m), style = MaterialTheme.typography.titleSmall)
        val months = (0..5).map { YearMonth.now().minusMonths(it.toLong()) }.reversed()
        val monthTotals = months.map { ym ->
            val rev = filtered.filter { it.type == TransactionType.REVENUE && it.date.startsWith(ym.toString()) }.sumOf { it.amountRon }
            val exp = filtered.filter { it.type == TransactionType.EXPENSE && it.date.startsWith(ym.toString()) }.sumOf { it.amountRon }
            Triple(ym.toString(), rev, exp)
        }
        val maxVal = (monthTotals.flatMap { listOf(it.second, it.third) }.maxOrNull() ?: 1L).toFloat()
        monthTotals.forEach { (label, revM, expM) ->
            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(label)
                Row(Modifier.fillMaxWidth()) {
                    val revFrac = (revM / maxVal).coerceAtMost(1f)
                    val expFrac = (expM / maxVal).coerceAtMost(1f)
                    Box(Modifier.weight(1f).padding(end = 8.dp)) {
                        Column {
                            Box(Modifier.fillMaxWidth(revFrac).height(8.dp).background(MaterialTheme.colorScheme.primary))
                            Spacer(Modifier.height(4.dp))
                            Box(Modifier.fillMaxWidth(expFrac).height(8.dp).background(MaterialTheme.colorScheme.error))
                        }
                    }
                    Column { Text(CurrencyRon.formatMinorUnits(revM)); Text(CurrencyRon.formatMinorUnits(expM)) }
                }
            }
        }
        // Project breakdown
        Spacer(Modifier.height(12.dp))
        Text(stringResource(id = com.predandrei.atelier.R.string.project_breakdown), style = MaterialTheme.typography.titleMedium)
        val inv by vm.inventory.collectAsState()
        val usage by vm.materialUsages.collectAsState()
        val materialsCost = remember(selectedProjectId, inv, usage) {
            if (selectedProjectId == null) 0L else usage.filter { it.projectId == selectedProjectId }.sumOf { u ->
                (inv.firstOrNull { it.id == u.inventoryItemId }?.priceRon ?: 0L) * u.quantityUsed
            }
        }
        val projectValue = selectedProjectId?.let { id -> projects.firstOrNull { it.id == id }?.valueRon } ?: 0L
        val paidForProject = remember(txs, selectedProjectId) {
            if (selectedProjectId == null) 0L else txs.filter { it.projectId == selectedProjectId && it.type == TransactionType.REVENUE }.sumOf { it.amountRon }
        }
        // Labor: from labor entries
        val laborEntries by vm.laborEntries.collectAsState()
        val laborCost = remember(laborEntries, selectedProjectId) {
            if (selectedProjectId == null) 0L else laborEntries.filter { it.projectId == selectedProjectId }
                .sumOf { (it.hourlyRateRon * it.minutes) / 60 }
        }
        val overheadCost = remember(txs, selectedProjectId) { if (selectedProjectId == null) 0L else txs.filter { it.projectId == selectedProjectId && it.type == TransactionType.EXPENSE && it.category.equals("OVERHEAD", true) }.sumOf { it.amountRon } }
        val remaining = (projectValue - paidForProject).coerceAtLeast(0)
        if (selectedProjectId != null) {
            Spacer(Modifier.height(8.dp))
            ElevatedCard { Column(Modifier.padding(12.dp)) {
                Text(stringResource(id = com.predandrei.atelier.R.string.materials_cost_fmt, CurrencyRon.formatMinorUnits(materialsCost)))
                Text(stringResource(id = com.predandrei.atelier.R.string.labor_cost_fmt, CurrencyRon.formatMinorUnits(laborCost)))
                Text(stringResource(id = com.predandrei.atelier.R.string.overhead_cost_fmt, CurrencyRon.formatMinorUnits(overheadCost)))
                Divider(Modifier.padding(vertical = 6.dp))
                Text(stringResource(id = com.predandrei.atelier.R.string.paid_fmt, CurrencyRon.formatMinorUnits(paidForProject)))
                Text(stringResource(id = com.predandrei.atelier.R.string.remaining_fmt, CurrencyRon.formatMinorUnits(remaining)))
            } }
        }

        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.id }) { t ->
                ListItem(
                    headlineContent = { Text("${if (t.type == TransactionType.REVENUE) stringResource(id = com.predandrei.atelier.R.string.revenue) else stringResource(id = com.predandrei.atelier.R.string.expense)} • ${t.category}") },
                    supportingContent = { Text(t.date) },
                    trailingContent = { Text(CurrencyRon.formatMinorUnits(t.amountRon)) }
                )
                Divider()
            }
        }
    }
}
