package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import com.predandrei.atelier.R

@Composable
fun SettingsScreen(
    vm: LanguageViewModel = hiltViewModel(),
    backupVm: BackupViewModel = hiltViewModel(),
    onOpenCategories: () -> Unit = {},
    onOpenSuppliers: () -> Unit = {},
    onOpenReports: () -> Unit = {}
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.language))
        Button(onClick = { vm.setLanguage("ro") }) { Text(stringResource(R.string.romanian)) }
        Button(onClick = { vm.setLanguage(null) }) { Text(stringResource(R.string.english)) }

        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.backup))
        Button(onClick = { backupVm.backup() }) { Text(stringResource(R.string.backup_to_file)) }
        Button(onClick = { backupVm.restore() }) { Text(stringResource(R.string.restore_from_backup)) }
        val status = backupVm.status.collectAsState().value
        status?.let { Text(it) }

        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.inventory_management))
        Button(onClick = onOpenCategories) { Text(stringResource(R.string.manage_categories)) }
        Button(onClick = onOpenSuppliers) { Text(stringResource(R.string.manage_suppliers)) }

        Spacer(Modifier.height(24.dp))
        Button(onClick = onOpenReports) { Text("Reports (PDF)") }
    }
}
