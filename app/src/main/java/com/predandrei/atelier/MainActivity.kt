package com.predandrei.atelier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.predandrei.atelier.navigation.RootNav

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AtelierAppRoot()
        }
    }
}

@Composable
fun AtelierAppRoot() {
    Surface(color = MaterialTheme.colorScheme.background) {
        // TODO: Navigation host and screens
        // Placeholder UI
        RootNav()
    }
}
