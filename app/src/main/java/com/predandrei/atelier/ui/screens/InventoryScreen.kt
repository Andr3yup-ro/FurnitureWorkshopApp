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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.data.model.InventoryItem
import com.predandrei.atelier.ui.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(modifier: Modifier = Modifier, onEdit: (Long?) -> Unit = {}, vm: InventoryViewModel = hiltViewModel()) {
    val itemsList by vm.items.collectAsState()
    var query by remember { mutableStateOf("") }
    Column(modifier.fillMaxSize()) {
        OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Search inventory") }, modifier = Modifier.padding(16.dp))
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filtered = itemsList.filter { it.name.contains(query, true) || it.category.name.contains(query, true) }
            items(filtered, key = { it.id }) { i ->
                InventoryRow(i, onClick = { onEdit(i.id) })
            }
        }
    }
}

@Composable
private fun InventoryRow(i: InventoryItem, onClick: () -> Unit) {
    ElevatedCard(modifier = Modifier.clickable { onClick() }) {
        Column(Modifier.padding(16.dp)) {
            Text(i.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Qty: ${i.quantity}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
