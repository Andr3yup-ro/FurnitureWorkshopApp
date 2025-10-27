package com.predandrei.atelier.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.appcompat.app.AppCompatDelegate

@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Language")
        Button(onClick = {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ro"))
        }) { Text("Română") }
        Button(onClick = {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        }) { Text("English") }
    }
}
