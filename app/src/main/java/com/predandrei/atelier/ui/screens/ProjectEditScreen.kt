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
import com.predandrei.atelier.data.model.Project
import com.predandrei.atelier.data.model.ProjectStatus
import com.predandrei.atelier.ui.viewmodel.ProjectsViewModel
import com.predandrei.atelier.ui.viewmodel.ClientsViewModel
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
    var selectedClientId by remember { mutableStateOf<Long?>(null) }
    var valueRonText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ProjectStatus.PENDING) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(projectId) {
        if (projectId != null && projectId > 0) {
            vm.get(projectId)?.let { p ->
                title = p.title
                description = p.description.orEmpty()
                selectedClientId = p.clientId.takeIf { it != 0L }
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
        // Client picker
        val clientsVm: ClientsViewModel = hiltViewModel()
        val clients by clientsVm.clients.collectAsState()
        var clientExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = clientExpanded, onExpandedChange = { clientExpanded = !clientExpanded }) {
            OutlinedTextField(
                readOnly = true,
                value = clients.firstOrNull { it.id == (selectedClientId ?: -1L) }?.name ?: "Select client",
                onValueChange = {},
                label = { Text("Client") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientExpanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = clientExpanded, onDismissRequest = { clientExpanded = false }) {
                DropdownMenuItem(text = { Text("No client") }, onClick = { selectedClientId = null; clientExpanded = false })
                clients.forEach { c ->
                    DropdownMenuItem(text = { Text(c.name) }, onClick = { selectedClientId = c.id; clientExpanded = false })
                }
            }
        }
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
            val clientId = selectedClientId ?: 0L
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
