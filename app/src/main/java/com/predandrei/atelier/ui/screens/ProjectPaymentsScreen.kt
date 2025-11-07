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
            totalText = String.format("%.2f", p.totalRon / 100.0)
            advanceText = String.format("%.2f", p.advanceRon / 100.0)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(id = com.predandrei.atelier.R.string.payment_plan), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = totalText,
                    onValueChange = { totalText = it.replace(',', '.').filter { it.isDigit() || it == '.' }.let { t ->
                        val first = t.indexOf('.')
                        if (first == -1) t else t.substring(0, first + 1) + t.substring(first + 1).replace(".", "")
                    } },
                    label = { Text(stringResource(id = com.predandrei.atelier.R.string.total_ron)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = advanceText,
                    onValueChange = { advanceText = it.replace(',', '.').filter { it.isDigit() || it == '.' }.let { t ->
                        val first = t.indexOf('.')
                        if (first == -1) t else t.substring(0, first + 1) + t.substring(first + 1).replace(".", "")
                    } },
                    label = { Text(stringResource(id = com.predandrei.atelier.R.string.advance_ron)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                val total = com.predandrei.atelier.util.MoneyParser.toMinorUnits(totalText)
                val adv = com.predandrei.atelier.util.MoneyParser.toMinorUnits(advanceText)
                val remaining = (total - adv).coerceAtLeast(0)
                Text(
                    text = "Remaining: ${CurrencyRon.formatMinorUnits(remaining)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    vm.upsertPlan(PaymentPlan(id = planState?.id ?: 0L, projectId = projectId, totalRon = total, advanceRon = adv))
                }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(id = com.predandrei.atelier.R.string.save_plan)) }
            }
        }

        Spacer(Modifier.height(16.dp))
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(id = com.predandrei.atelier.R.string.installments), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                // Simple add installment row
                var dueDate by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
                var amountText by remember { mutableStateOf("") }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text(stringResource(id = com.predandrei.atelier.R.string.due_date)) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.replace(',', '.').filter { it.isDigit() || it == '.' }.let { t ->
                            val first = t.indexOf('.')
                            if (first == -1) t else t.substring(0, first + 1) + t.substring(first + 1).replace(".", "")
                        } },
                        label = { Text(stringResource(id = com.predandrei.atelier.R.string.amount_ron)) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    val planId = planState?.id ?: return@Button
                    val amt = com.predandrei.atelier.util.MoneyParser.toMinorUnits(amountText)
                    if (amt > 0) vm.upsertInstallments(Installment(planId = planId, dueDate = dueDate, amountRon = amt))
                    amountText = ""
                }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(id = com.predandrei.atelier.R.string.add)) }
            }
        }

        Spacer(Modifier.height(16.dp))
        val currentPlan = planState
        if (currentPlan != null) {
            val inst by vm.installments(currentPlan.id).collectAsState()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(inst, key = { it.id }) { ins ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(ins.dueDate, style = MaterialTheme.typography.titleSmall)
                                Text(CurrencyRon.formatMinorUnits(ins.amountRon), style = MaterialTheme.typography.titleSmall)
                            }
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(onClick = {}, label = { Text(if (ins.paid) stringResource(id = com.predandrei.atelier.R.string.paid) else stringResource(id = com.predandrei.atelier.R.string.unpaid)) })
                                if (!ins.paid) Button(onClick = { vm.markPaid(ins, projectId) }) { Text(stringResource(id = com.predandrei.atelier.R.string.mark_paid)) }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text(stringResource(id = com.predandrei.atelier.R.string.done)) }
    }
}
