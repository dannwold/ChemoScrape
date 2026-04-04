package com.app.dorkscraper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.dorkscraper.data.model.ScraperOptions

@Composable
fun InputForm(
    isScraping: Boolean,
    onStart: (ScraperOptions) -> Unit,
    onStop: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var depth by remember { mutableFloatStateOf(1f) }
    var scrapeImages by remember { mutableStateOf(true) }
    var scrapeJson by remember { mutableStateOf(true) }
    var scrapeVideos by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Dork/URL Query") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isScraping
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Depth: ${depth.toInt()}", modifier = Modifier.width(80.dp))
            Slider(
                value = depth,
                onValueChange = { depth = it },
                valueRange = 0f..5f,
                steps = 4,
                modifier = Modifier.weight(1f),
                enabled = !isScraping
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilterCheckbox("Images", scrapeImages, !isScraping) { scrapeImages = it }
            FilterCheckbox("JSON", scrapeJson, !isScraping) { scrapeJson = it }
            FilterCheckbox("Videos", scrapeVideos, !isScraping) { scrapeVideos = it }
        }

        Button(
            onClick = {
                if (isScraping) onStop()
                else onStart(ScraperOptions(query, depth.toInt(), scrapeImages, scrapeVideos, scrapeJson))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = if (isScraping) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                     else ButtonDefaults.buttonColors()
        ) {
            Text(if (isScraping) "STOP SCRAPING" else "START SCRAPING")
        }
    }
}

@Composable
fun FilterCheckbox(label: String, checked: Boolean, enabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
        Text(label)
    }
}
