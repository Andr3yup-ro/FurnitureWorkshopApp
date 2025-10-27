package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.Installment
import com.predandrei.atelier.data.model.PaymentPlan
import com.predandrei.atelier.data.model.PaymentMethod
import com.predandrei.atelier.ui.viewmodel.PaymentsViewModel
import com.predandrei.atelier.util.CurrencyRon

@Composable
fun ProjectPaymentsScreen(projectId: Long, onDone: () -> Unit, vm: PaymentsViewModel = hiltViewModel()) {
    val planState by vm.planForProject(projectId).collectAsState()
    var totalText by remember { mutableStateOf("") }
    var advanceText by remember { mutableStateOf("") }
    var installments by remember { mutableStateOf(listOf<Installment>()) }

    LaunchedEffect(planState) {
        planState?.let { p ->
            totalText = p.totalRon.toString()
            advanceText = p.advanceRon.toString()
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Payment Plan", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = totalText, onValueChange = { totalText = it.filter { ch -> ch.isDigit() } }, label = { Text("Total (bani)") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = advanceText, onValueChange = { advanceText = it.filter { ch -> ch.isDigit() } }, label = { Text("Advance (bani)") })
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val total = totalText.toLongOrNull() ?: 0L
            val adv = advanceText.toLongOrNull() ?: 0L
            vm.upsertPlan(PaymentPlan(id = planState?.id ?: 0L, projectId = projectId, totalRon = total, advanceRon = adv))
        }) { Text("Save Plan") }

        Spacer(Modifier.height(12.dp))
        Text("Installments")
        Spacer(Modifier.height(8.dp))
        // Simple add installment row
        var dueDate by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
        var amountText by remember { mutableStateOf("") }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due date (YYYY-MM-DD)") })
            OutlinedTextField(value = amountText, onValueChange = { amountText = it.filter { ch -> ch.isDigit() } }, label = { Text("Amount (bani)") })
            Button(onClick = {
                val planId = planState?.id ?: return@Button
                val amt = amountText.toLongOrNull() ?: 0L
                if (amt > 0) vm.upsertInstallments(Installment(planId = planId, dueDate = dueDate, amountRon = amt))
                amountText = ""
            }) { Text("Add") }
        }

        Spacer(Modifier.height(8.dp))
        val currentPlan = planState
        if (currentPlan != null) {
            val inst by vm.installments(currentPlan.id).collectAsState()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(inst, key = { it.id }) { ins ->
                    ListItem(
                        headlineContent = { Text("${ins.dueDate}") },
                        supportingContent = { Text(if (ins.paid) "PAID" else "UNPAID") },
                        trailingContent = { Text(CurrencyRon.formatMinorUnits(ins.amountRon)) }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                        if (!ins.paid) Button(onClick = { vm.markPaid(ins, projectId) }) { Text("Mark paid") }
                    }
                    Divider()
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onDone) { Text("Done") }
    }
}
