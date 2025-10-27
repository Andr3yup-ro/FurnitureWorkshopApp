package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.InventoryCategory
import com.predandrei.atelier.data.model.InventoryItem
import com.predandrei.atelier.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryEditScreen(itemId: Long?, onSaved: () -> Unit, vm: InventoryViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var categoryText by remember { mutableStateOf(InventoryCategory.ACCESSORIES.name) }
    var qtyText by remember { mutableStateOf("") }
    var minText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(itemId) {
        if (itemId != null && itemId > 0) {
            vm.get(itemId)?.let { i ->
                name = i.name
                categoryText = i.category.name
                qtyText = i.quantity.toString()
                minText = i.minStock.toString()
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = categoryText, onValueChange = { categoryText = it }, label = { Text("Category (e.g., CHAIRS)") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = qtyText, onValueChange = { qtyText = it.filter { ch -> ch.isDigit() } }, label = { Text("Quantity") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = minText, onValueChange = { minText = it.filter { ch -> ch.isDigit() } }, label = { Text("Min stock") })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val qty = qtyText.toIntOrNull() ?: 0
            val min = minText.toIntOrNull() ?: 0
            val cat = runCatching { InventoryCategory.valueOf(categoryText) }.getOrElse { InventoryCategory.ACCESSORIES }
            val item = InventoryItem(id = itemId ?: 0L, name = name, category = cat, quantity = qty, minStock = min, supplierId = null)
            scope.launch { vm.save(item); onSaved() }
        }) { Text("Save") }

        if ((itemId ?: 0L) > 0) {
            Spacer(Modifier.height(16.dp))
            var confirm by remember { mutableStateOf(false) }
            if (confirm) {
                AlertDialog(
                    onDismissRequest = { confirm = false },
                    confirmButton = { TextButton(onClick = { scope.launch { vm.delete(itemId!!); onSaved() } }) { Text("Delete") } },
                    dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } },
                    title = { Text("Delete item?") },
                    text = { Text("This action cannot be undone.") }
                )
            }
            OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), onClick = { confirm = true }) { Text("Delete") }
        }
    }
}
