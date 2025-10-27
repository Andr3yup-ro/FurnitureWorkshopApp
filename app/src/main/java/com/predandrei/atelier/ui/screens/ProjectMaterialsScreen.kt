package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.ProjectMaterialUsage
import com.predandrei.atelier.ui.viewmodel.ProjectMaterialsViewModel
import com.predandrei.atelier.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectMaterialsScreen(projectId: Long, onDone: () -> Unit, vm: ProjectMaterialsViewModel = hiltViewModel(), invVm: InventoryViewModel = hiltViewModel()) {
    val usage by vm.usage(projectId).collectAsState()
    val items by invVm.items.collectAsState()

    var selectedItemId by remember { mutableStateOf<Long?>(items.firstOrNull()?.id) }
    var qtyText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Materials used", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        // Item picker
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = items.firstOrNull { it.id == selectedItemId }?.name ?: "Select item",
                onValueChange = {}, readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { itx ->
                    DropdownMenuItem(text = { Text(itx.name) }, onClick = { selectedItemId = itx.id; expanded = false })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = qtyText, onValueChange = { qtyText = it.filter { ch -> ch.isDigit() } }, label = { Text("Quantity used") })
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val id = selectedItemId ?: return@Button
            val qty = qtyText.toIntOrNull() ?: 0
            if (qty > 0) {
                vm.addUsage(ProjectMaterialUsage(projectId = projectId, inventoryItemId = id, quantityUsed = qty, date = java.time.LocalDate.now().toString()))
                qtyText = ""
            }
        }) { Text("Add usage") }

        Spacer(Modifier.height(12.dp))
        val lowStockCount = items.count { it.quantity <= it.minStock }
        Text("Low stock items (global): $lowStockCount")

        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(usage, key = { it.id }) { u ->
                val item = items.firstOrNull { it.id == u.inventoryItemId }
                ListItem(
                    headlineContent = { Text(item?.name ?: "Item ${u.inventoryItemId}") },
                    supportingContent = { Text("${u.quantityUsed} used on ${u.date}") }
                )
                Divider()
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onDone) { Text("Done") }
    }
}
