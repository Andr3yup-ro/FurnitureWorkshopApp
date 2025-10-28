package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.Supplier
import com.predandrei.atelier.ui.viewmodel.SuppliersViewModel
import kotlinx.coroutines.launch

@Composable
fun SupplierEditScreen(supplierId: Long?, onSaved: () -> Unit, vm: SuppliersViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(supplierId) {
        if (supplierId != null && supplierId > 0) {
            vm.get(supplierId)?.let { s ->
                name = s.name
                contact = s.contact.orEmpty()
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact") })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch { vm.save(Supplier(id = supplierId ?: 0L, name = name, contact = contact.ifBlank { null })); onSaved() }
        }) { Text("Save") }
        if ((supplierId ?: 0L) > 0) {
            Spacer(Modifier.height(16.dp))
            var confirm by remember { mutableStateOf(false) }
            if (confirm) {
                AlertDialog(
                    onDismissRequest = { confirm = false },
                    confirmButton = { TextButton(onClick = { scope.launch { vm.delete(supplierId!!); onSaved() } }) { Text("Delete") } },
                    dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } },
                    title = { Text("Delete supplier?") },
                    text = { Text("This action cannot be undone.") }
                )
            }
            OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), onClick = { confirm = true }) { Text("Delete") }
        }
    }
}
