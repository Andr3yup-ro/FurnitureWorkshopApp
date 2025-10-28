package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.InventoryItem
import com.predandrei.atelier.ui.viewmodel.InventoryViewModel
import com.predandrei.atelier.ui.viewmodel.CategoriesViewModel
import com.predandrei.atelier.ui.viewmodel.SuppliersViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryEditScreen(itemId: Long?, onSaved: () -> Unit, vm: InventoryViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var qtyText by remember { mutableStateOf("") }
    var minText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var partNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSupplierId by remember { mutableStateOf<Long?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(itemId) {
        if (itemId != null && itemId > 0) {
            vm.get(itemId)?.let { i ->
                name = i.name
                selectedCategoryId = i.categoryId
                qtyText = i.quantity.toString()
                minText = i.minStock.toString()
                // Display as decimal (RON)
                priceText = String.format("%.2f", i.priceRon / 100.0)
                partNumber = i.partNumber.orEmpty()
                description = i.description.orEmpty()
                selectedSupplierId = i.supplierId
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.name)) })
        Spacer(Modifier.height(8.dp))
        val catVm: CategoriesViewModel = hiltViewModel()
        val cats by catVm.categories.collectAsState()
        var catExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = !catExpanded }) {
            OutlinedTextField(
                readOnly = true,
                value = cats.firstOrNull { it.id == selectedCategoryId }?.name ?: stringResource(id = com.predandrei.atelier.R.string.no_category),
                onValueChange = {},
                label = { Text(stringResource(id = com.predandrei.atelier.R.string.category)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                DropdownMenuItem(text = { Text(stringResource(id = com.predandrei.atelier.R.string.no_category)) }, onClick = { selectedCategoryId = null; catExpanded = false })
                cats.forEach { c -> DropdownMenuItem(text = { Text(c.name) }, onClick = { selectedCategoryId = c.id; catExpanded = false }) }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = qtyText, onValueChange = { qtyText = it.filter { ch -> ch.isDigit() } }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.quantity)) })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = priceText, onValueChange = { priceText = it.replace(',', '.').filter { it.isDigit() || it == '.' }.let { t ->
            // keep only one dot
            val first = t.indexOf('.')
            if (first == -1) t else t.substring(0, first + 1) + t.substring(first + 1).replace(".", "")
        } }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.price_ron)) })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = partNumber, onValueChange = { partNumber = it }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.part_number)) })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.description)) })
        Spacer(Modifier.height(8.dp))
        val supVm: SuppliersViewModel = hiltViewModel()
        val sups by supVm.suppliers.collectAsState()
        var supExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = supExpanded, onExpandedChange = { supExpanded = !supExpanded }) {
            OutlinedTextField(
                readOnly = true,
                value = sups.firstOrNull { it.id == selectedSupplierId }?.name ?: stringResource(id = com.predandrei.atelier.R.string.no_supplier),
                onValueChange = {},
                label = { Text(stringResource(id = com.predandrei.atelier.R.string.supplier)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supExpanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = supExpanded, onDismissRequest = { supExpanded = false }) {
                DropdownMenuItem(text = { Text(stringResource(id = com.predandrei.atelier.R.string.no_supplier)) }, onClick = { selectedSupplierId = null; supExpanded = false })
                sups.forEach { s -> DropdownMenuItem(text = { Text(s.name) }, onClick = { selectedSupplierId = s.id; supExpanded = false }) }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = minText, onValueChange = { minText = it.filter { ch -> ch.isDigit() } }, label = { Text(stringResource(id = com.predandrei.atelier.R.string.min_stock)) })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val qty = qtyText.toIntOrNull() ?: 0
            val min = minText.toIntOrNull() ?: 0
            val price = com.predandrei.atelier.util.MoneyParser.toMinorUnits(priceText)
            val item = InventoryItem(
                id = itemId ?: 0L,
                name = name,
                categoryId = selectedCategoryId,
                quantity = qty,
                priceRon = price,
                partNumber = partNumber.ifBlank { null },
                description = description.ifBlank { null },
                minStock = min,
                supplierId = selectedSupplierId
            )
            scope.launch { vm.save(item); onSaved() }
        }) { Text(stringResource(id = com.predandrei.atelier.R.string.save)) }

        if ((itemId ?: 0L) > 0) {
            Spacer(Modifier.height(16.dp))
            var confirm by remember { mutableStateOf(false) }
            if (confirm) {
                AlertDialog(
                    onDismissRequest = { confirm = false },
                    confirmButton = { TextButton(onClick = { scope.launch { vm.delete(itemId!!); onSaved() } }) { Text(stringResource(id = com.predandrei.atelier.R.string.delete)) } },
                    dismissButton = { TextButton(onClick = { confirm = false }) { Text(stringResource(id = com.predandrei.atelier.R.string.cancel)) } },
                    title = { Text(stringResource(id = com.predandrei.atelier.R.string.delete_item_q)) },
                    text = { Text(stringResource(id = com.predandrei.atelier.R.string.action_cannot_undone)) }
                )
            }
            OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), onClick = { confirm = true }) { Text(stringResource(id = com.predandrei.atelier.R.string.delete)) }
        }
    }
}
