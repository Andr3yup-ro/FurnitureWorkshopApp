package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.ui.viewmodel.PaymentsViewModel
import com.predandrei.atelier.util.CurrencyRon

@Composable
fun PaymentsScreen(vm: PaymentsViewModel = hiltViewModel()) {
    var showOnlyUnpaid by remember { mutableStateOf(true) }
    val plans by vm.allPlans.collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Pending Payments", style = MaterialTheme.typography.titleMedium)
            AssistChip(onClick = { showOnlyUnpaid = !showOnlyUnpaid }, label = { Text(if (showOnlyUnpaid) "Showing Unpaid" else "Showing All") })
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(plans, key = { it.id }) { plan ->
                val installments by vm.installments(plan.id).collectAsState(initial = emptyList())
                val list = if (showOnlyUnpaid) installments.filter { !it.paid } else installments
                if (list.isNotEmpty()) {
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Plan #${plan.id}", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(6.dp))
                            list.forEach { ins ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(ins.dueDate)
                                    Text(CurrencyRon.formatMinorUnits(ins.amountRon))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
