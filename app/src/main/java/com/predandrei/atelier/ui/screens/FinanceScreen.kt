package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.FinancialTransaction
import com.predandrei.atelier.data.model.TransactionType
import com.predandrei.atelier.ui.viewmodel.FinanceViewModel
import com.predandrei.atelier.util.CurrencyRon

@Composable
fun FinanceScreen(modifier: Modifier = Modifier, vm: FinanceViewModel = hiltViewModel()) {
    val txs by vm.transactions.collectAsState()
    val revenue by vm.revenueTotalRon.collectAsState()
    val expenses by vm.expenseTotalRon.collectAsState()
    val profit by vm.profitRon.collectAsState()

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

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
                // Simple toggle for brevity
                Button(onClick = { type = if (type == TransactionType.REVENUE) TransactionType.EXPENSE else TransactionType.REVENUE }) {
                    Text(type.name)
                }
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
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(txs, key = { it.id }) { t ->
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
