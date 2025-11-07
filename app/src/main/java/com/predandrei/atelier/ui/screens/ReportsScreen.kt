package com.predandrei.atelier.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.predandrei.atelier.ui.viewmodel.ReportsViewModel

@Composable
fun ReportsScreen(vm: ReportsViewModel = hiltViewModel()) {
    val ctx = LocalContext.current
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val createDocLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) {
            vm.exportAll(uri, ctx.contentResolver)
            pendingUri = uri
        }
    }

    val status by vm.status.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { createDocLauncher.launch("manager-prestan-report.pdf") }) {
            Text("Generate all data (PDF)")
        }
        status?.let { Text(it, modifier = Modifier.padding(top = 12.dp)) }
        if (pendingUri != null) {
            Text("Saved to: ${pendingUri}", modifier = Modifier.padding(top = 4.dp))
        }
    }
}
