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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.ui.viewmodel.SuppliersViewModel

@Composable
fun SuppliersScreen(onEdit: (Long?) -> Unit, vm: SuppliersViewModel = hiltViewModel()) {
    val list by vm.suppliers.collectAsState()
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { onEdit(null) }) { Icon(Icons.Rounded.Add, contentDescription = "Add supplier") }
    }) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list, key = { it.id }) { s ->
                ElevatedCard(Modifier.clickable { onEdit(s.id) }) {
                    Column(Modifier.padding(16.dp)) { Text(s.name) }
                }
            }
        }
    }
}
