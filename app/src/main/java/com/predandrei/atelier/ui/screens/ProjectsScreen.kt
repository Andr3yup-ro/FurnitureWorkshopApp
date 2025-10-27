package com.predandrei.atelier.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProjectsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val projects = sampleProjects()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(projects) { p ->
            ElevatedCard(
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Open project: ${p.name}", Toast.LENGTH_SHORT).show()
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Rounded.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(p.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(p.client, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(p.status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

private data class ProjectPreview(val name: String, val client: String, val status: String)

private fun sampleProjects() = listOf(
    ProjectPreview("Kitchen - L shape", "Client: Ionescu Maria", "In progress"),
    ProjectPreview("Wardrobe 3 doors", "Client: Popescu Andrei", "Design review"),
    ProjectPreview("Office desk", "Client: SC Tech SRL", "Awaiting materials"),
    ProjectPreview("TV cabinet", "Client: Dumitru Ioan", "Ready for delivery"),
)
