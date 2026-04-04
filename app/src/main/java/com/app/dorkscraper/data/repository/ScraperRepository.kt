package com.app.dorkscraper.data.repository

import com.app.dorkscraper.data.model.ScrapedItem
import com.app.dorkscraper.data.model.ScraperOptions
import com.app.dorkscraper.data.remote.SearchOrchestrator
import kotlinx.coroutines.flow.Flow

class ScraperRepository(private val searchOrchestrator: SearchOrchestrator) {

    suspend fun startScrape(options: ScraperOptions, onLog: (String) -> Unit): Flow<ScrapedItem> {
        return searchOrchestrator.startScraping(options, onLog)
    }
}
