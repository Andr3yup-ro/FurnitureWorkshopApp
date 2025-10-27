package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.background
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
        Text("Revenue: ${CurrencyRon.formatMinorUnits(revenue)}")
        Text("Expenses: ${CurrencyRon.formatMinorUnits(expenses)}")
        Text("Profit: ${CurrencyRon.formatMinorUnits(profit)}", color = MaterialTheme.colorScheme.primary)

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
            // Simple toggle
            Button(onClick = { type = if (type == TransactionType.REVENUE) TransactionType.EXPENSE else TransactionType.REVENUE }) {
                Text(type.name)
            }
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
            OutlinedTextField(value = amountText, onValueChange = { amountText = it.filter { ch -> ch.isDigit() } }, label = { Text("Amount (bani)") })
            Button(onClick = {
                val amt = amountText.toLongOrNull() ?: 0L
                if (amt > 0) vm.save(
                    FinancialTransaction(projectId = null, type = type, category = category.ifBlank { "GENERAL" }, amountRon = amt, date = java.time.LocalDate.now().toString())
                )
                amountText = ""; category = ""
            }) { Text("Add") }
        }

        Spacer(Modifier.height(12.dp))
        // Filter row
        var projExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = projExpanded, onExpandedChange = { projExpanded = !projExpanded }) {
            OutlinedTextField(
                readOnly = true,
                value = selectedProjectId?.let { id -> projects.firstOrNull { it.id == id }?.title } ?: "All projects",
                onValueChange = {},
                label = { Text("Project filter") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projExpanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = projExpanded, onDismissRequest = { projExpanded = false }) {
                DropdownMenuItem(text = { Text("All projects") }, onClick = { selectedProjectId = null; projExpanded = false })
                projects.forEach { p -> DropdownMenuItem(text = { Text(p.title) }, onClick = { selectedProjectId = p.id; projExpanded = false }) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start YYYY-MM-DD") })
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("End YYYY-MM-DD") })
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
        Text("Overview (last 6 months)", style = MaterialTheme.typography.titleSmall)
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
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.id }) { t ->
                ListItem(
                    headlineContent = { Text("${t.type.name} • ${t.category}") },
                    supportingContent = { Text(t.date) },
                    trailingContent = { Text(CurrencyRon.formatMinorUnits(t.amountRon)) }
                )
                Divider()
            }
        }
    }
}
