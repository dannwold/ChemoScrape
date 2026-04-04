package com.app.dorkscraper.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dorkscraper.data.model.ItemType
import com.app.dorkscraper.data.model.ScrapedItem
import com.app.dorkscraper.data.model.ScraperOptions
import com.app.dorkscraper.data.repository.ScraperRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ScraperViewModel(private val repository: ScraperRepository) : ViewModel() {

    private val _scrapedItems = mutableStateListOf<ScrapedItem>()
    val scrapedItems: List<ScrapedItem> = _scrapedItems

    private val _logs = mutableStateListOf<String>()
    val logs: List<String> = _logs

    private val _isScraping = mutableStateOf(false)
    val isScraping: State<Boolean> = _isScraping

    private var scrapeJob: Job? = null

    fun startScraping(options: ScraperOptions) {
        if (_isScraping.value) return

        _isScraping.value = true
        _scrapedItems.clear()
        _logs.clear()

        addLog("Starting scrape for: ${options.query}")

        scrapeJob = viewModelScope.launch {
            repository.startScrape(options) { log ->
                addLog(log)
            }
            .catch { e ->
                addLog("Error: ${e.message}")
                _isScraping.value = false
            }
            .collect { item ->
                _scrapedItems.add(item)
            }

            _isScraping.value = false
            addLog("Scraping complete. Found ${_scrapedItems.size} items.")
        }
    }

    fun stopScraping() {
        scrapeJob?.cancel()
        _isScraping.value = false
        addLog("Scraping stopped by user.")
    }

    private fun addLog(message: String) {
        _logs.add(0, message) // Add to top for console view
    }
}
