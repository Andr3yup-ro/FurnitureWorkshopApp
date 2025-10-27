package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.Client
import com.predandrei.atelier.ui.viewmodel.ClientsViewModel
import kotlinx.coroutines.launch

@Composable
fun ClientEditScreen(clientId: Long?, onSaved: () -> Unit, vm: ClientsViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(clientId) {
        if (clientId != null && clientId > 0) {
            vm.get(clientId)?.let { c ->
                name = c.name; phone = c.phone.orEmpty(); email = c.email.orEmpty(); address = c.address.orEmpty()
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val c = Client(
                id = clientId ?: 0L,
                name = name,
                phone = phone.ifBlank { null },
                email = email.ifBlank { null },
                address = address.ifBlank { null }
            )
            scope.launch { vm.save(c); onSaved() }
        }) { Text("Save") }
    }
}
