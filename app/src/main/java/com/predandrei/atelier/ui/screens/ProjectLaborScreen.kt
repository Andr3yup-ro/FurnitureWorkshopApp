package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.R
import com.predandrei.atelier.data.model.LaborEntry
import com.predandrei.atelier.ui.viewmodel.LaborViewModel
import com.predandrei.atelier.util.CurrencyRon

@Composable
fun ProjectLaborScreen(projectId: Long, onDone: () -> Unit, vm: LaborViewModel = hiltViewModel()) {
    val entries by vm.entries(projectId).collectAsState(initial = emptyList())
    val totalMinutes by vm.totalMinutes(projectId).collectAsState()
    val totalCost by vm.totalCostRon(projectId).collectAsState()

    var date by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var hoursText by remember { mutableStateOf("") }
    var minutesText by remember { mutableStateOf("") }
    var rateText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.labor_overview), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.labor_total_hours_fmt, totalMinutes / 60, totalMinutes % 60))
        Text(stringResource(R.string.labor_total_cost_fmt, CurrencyRon.formatMinorUnits(totalCost)))

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text(stringResource(R.string.due_date)) })
            OutlinedTextField(value = hoursText, onValueChange = { hoursText = it.filter { ch -> ch.isDigit() } }, label = { Text(stringResource(R.string.hours)) })
            OutlinedTextField(value = minutesText, onValueChange = { minutesText = it.filter { ch -> ch.isDigit() } }, label = { Text(stringResource(R.string.minutes)) })
            OutlinedTextField(value = rateText, onValueChange = {
                rateText = it.replace(',', '.').filter { ch -> ch.isDigit() || ch == '.' }.let { t ->
                    val first = t.indexOf('.')
                    if (first == -1) t else t.substring(0, first + 1) + t.substring(first + 1).replace(".", "")
                }
            }, label = { Text(stringResource(R.string.hourly_rate_ron)) })
            Button(onClick = {
                val minutes = (hoursText.toIntOrNull() ?: 0) * 60 + (minutesText.toIntOrNull() ?: 0)
                val rate = com.predandrei.atelier.util.MoneyParser.toMinorUnits(rateText)
                if (minutes > 0 && rate > 0) {
                    vm.save(LaborEntry(projectId = projectId, date = date, minutes = minutes, hourlyRateRon = rate))
                    hoursText = ""; minutesText = ""; rateText = ""
                }
            }) { Text(stringResource(R.string.add)) }
        }

        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(entries, key = { it.id }) { e ->
                val cost = (e.hourlyRateRon * e.minutes) / 60
                ListItem(
                    headlineContent = { Text("${e.date} • ${e.minutes / 60}h ${(e.minutes % 60)}m") },
                    trailingContent = { Text(CurrencyRon.formatMinorUnits(cost)) }
                )
                var confirm by remember { mutableStateOf(false) }
                if (confirm) {
                    AlertDialog(
                        onDismissRequest = { confirm = false },
                        confirmButton = { TextButton(onClick = { vm.delete(e.id); confirm = false }) { Text(stringResource(R.string.delete)) } },
                        dismissButton = { TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.cancel)) } },
                        title = { Text(stringResource(R.string.delete_labor_q)) },
                        text = { Text(stringResource(R.string.action_cannot_undone)) }
                    )
                }
                TextButton(onClick = { confirm = true }) { Text(stringResource(R.string.delete)) }
                Divider()
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = onDone) { Text(stringResource(R.string.done)) }
    }
}
