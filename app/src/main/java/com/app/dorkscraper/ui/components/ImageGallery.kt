package com.app.dorkscraper.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.dorkscraper.data.model.ItemType
import com.app.dorkscraper.data.model.ScrapedItem

@Composable
fun ImageGallery(
    items: List<ScrapedItem>,
    onImageClick: (String) -> Unit
) {
    val images = items.filter { it.type == ItemType.IMAGE }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(images) { item ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Scraped Image",
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(2.dp)
                    .clickable { onImageClick(item.url) },
                contentScale = ContentScale.Crop
            )
        }
    }
}
