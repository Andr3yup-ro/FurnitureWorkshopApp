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
import com.predandrei.atelier.ui.viewmodel.BackupViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun SettingsScreen(vm: LanguageViewModel = hiltViewModel(), backupVm: BackupViewModel = hiltViewModel()) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Language")
        Button(onClick = { vm.setLanguage("ro") }) { Text("Română") }
        Button(onClick = { vm.setLanguage(null) }) { Text("English") }

        Spacer(Modifier.height(24.dp))
        Text("Backup")
        Button(onClick = { backupVm.backup() }) { Text("Backup to file") }
        Button(onClick = { backupVm.restore() }) { Text("Restore from backup") }
        val status = backupVm.status.collectAsState().value
        status?.let { Text(it) }
    }
}
