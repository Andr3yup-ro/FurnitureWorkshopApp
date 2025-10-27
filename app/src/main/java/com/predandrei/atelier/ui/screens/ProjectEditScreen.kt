package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.Project
import com.predandrei.atelier.data.model.ProjectStatus
import com.predandrei.atelier.ui.viewmodel.ProjectsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectEditScreen(
    projectId: Long?,
    onSaved: () -> Unit,
    vm: ProjectsViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var clientIdText by remember { mutableStateOf("") }
    var valueRonText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ProjectStatus.PENDING) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(projectId) {
        if (projectId != null && projectId > 0) {
            vm.get(projectId)?.let { p ->
                title = p.title
                description = p.description.orEmpty()
                clientIdText = p.clientId.toString()
                valueRonText = p.valueRon.toString()
                status = p.status
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = clientIdText, onValueChange = { clientIdText = it }, label = { Text("Client ID") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = valueRonText, onValueChange = { valueRonText = it.filter { ch -> ch.isDigit() } }, label = { Text("Value (RON bani)") })
        Spacer(Modifier.height(8.dp))
        Text("Status: ${status.name}")
        var expanded by remember { mutableStateOf(false) }
        Button(onClick = { expanded = true }) { Text("Change status") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ProjectStatus.values().forEach { s ->
                DropdownMenuItem(text = { Text(s.name) }, onClick = { status = s; expanded = false })
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val clientId = clientIdText.toLongOrNull() ?: 0L
            val valueRon = valueRonText.toLongOrNull() ?: 0L
            val p = Project(
                id = projectId ?: 0L,
                clientId = clientId,
                title = title,
                description = description.ifBlank { null },
                status = status,
                valueRon = valueRon,
                deadline = null
            )
            scope.launch {
                vm.save(p)
                onSaved()
            }
        }) { Text("Save") }
    }
}
