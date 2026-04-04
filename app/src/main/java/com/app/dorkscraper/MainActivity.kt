package com.app.dorkscraper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.app.dorkscraper.data.remote.SearchOrchestrator
import com.app.dorkscraper.data.remote.WebViewScraper
import com.app.dorkscraper.data.repository.ScraperRepository
import com.app.dorkscraper.ui.components.*
import com.app.dorkscraper.ui.viewmodel.ScraperViewModel
import com.app.dorkscraper.util.DownloadHelper

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ScraperViewModel
    private lateinit var downloadHelper: DownloadHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manual DI for simplicity in this project
        val webViewScraper = WebViewScraper(this)
        val searchOrchestrator = SearchOrchestrator(webViewScraper)
        val repository = ScraperRepository(searchOrchestrator)
        viewModel = ScraperViewModel(repository)
        downloadHelper = DownloadHelper(this)

        setContent {
            var selectedImageUrl by remember { mutableStateOf<String?>(null) }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        InputForm(
                            isScraping = viewModel.isScraping.value,
                            onStart = { viewModel.startScraping(it) },
                            onStop = { viewModel.stopScraping() }
                        )

                        ConsoleLog(logs = viewModel.logs)

                        Divider()

                        ImageGallery(
                            items = viewModel.scrapedItems,
                            onImageClick = { selectedImageUrl = it }
                        )
                    }

                    FullscreenViewer(
                        imageUrl = selectedImageUrl,
                        onDismiss = { selectedImageUrl = null },
                        onDownload = { downloadHelper.downloadImage(it) }
                    )
                }
            }
        }
    }
}
