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
import com.predandrei.atelier.data.model.Category
import com.predandrei.atelier.ui.viewmodel.CategoriesViewModel
import kotlinx.coroutines.launch

@Composable
fun CategoryEditScreen(categoryId: Long?, onSaved: () -> Unit, vm: CategoriesViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(categoryId) {
        if (categoryId != null && categoryId > 0) {
            vm.get(categoryId)?.let { name = it.name }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch { vm.save(Category(id = categoryId ?: 0L, name = name)); onSaved() }
        }) { Text("Save") }
        if ((categoryId ?: 0L) > 0) {
            Spacer(Modifier.height(16.dp))
            var confirm by remember { mutableStateOf(false) }
            if (confirm) {
                AlertDialog(
                    onDismissRequest = { confirm = false },
                    confirmButton = { TextButton(onClick = { scope.launch { vm.delete(categoryId!!); onSaved() } }) { Text("Delete") } },
                    dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } },
                    title = { Text("Delete category?") },
                    text = { Text("This action cannot be undone.") }
                )
            }
            OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), onClick = { confirm = true }) { Text("Delete") }
        }
    }
}
