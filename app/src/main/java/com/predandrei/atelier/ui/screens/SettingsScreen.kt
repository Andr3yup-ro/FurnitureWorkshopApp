package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.ui.viewmodel.LanguageViewModel

@Composable
fun SettingsScreen(vm: LanguageViewModel = hiltViewModel()) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Language")
        Button(onClick = { vm.setLanguage("ro") }) { Text("Română") }
        Button(onClick = { vm.setLanguage(null) }) { Text("English") }
    }
}
