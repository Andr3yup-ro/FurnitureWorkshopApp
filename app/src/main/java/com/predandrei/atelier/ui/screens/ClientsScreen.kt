package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.Client
import com.predandrei.atelier.ui.viewmodel.ClientsViewModel

@Composable
fun ClientsScreen(modifier: Modifier = Modifier, onEdit: (Long?) -> Unit = {}, vm: ClientsViewModel = hiltViewModel()) {
    val clientList by vm.clients.collectAsState()
    var query by remember { mutableStateOf("") }
    Column(modifier.fillMaxSize()) {
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Search clients") }, modifier = Modifier.padding(16.dp))
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filtered = clientList.filter { it.name.contains(query, true) || it.phone.orEmpty().contains(query, true) || it.email.orEmpty().contains(query, true) }
            items(filtered, key = { it.id }) { c ->
                ClientRow(c, onClick = { onEdit(c.id) })
            }
        }
    }
}

@Composable
private fun ClientRow(c: Client, onClick: () -> Unit) {
    ElevatedCard(modifier = Modifier.clickable { onClick() }) {
        Column(Modifier.padding(16.dp)) {
            Text(c.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            c.phone?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            c.email?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
