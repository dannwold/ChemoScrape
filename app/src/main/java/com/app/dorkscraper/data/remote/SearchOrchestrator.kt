package com.app.dorkscraper.data.remote

import com.app.dorkscraper.data.model.ItemType
import com.app.dorkscraper.data.model.ScrapedItem
import com.app.dorkscraper.data.model.ScraperOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException

class SearchOrchestrator(private val webViewScraper: WebViewScraper) {

    private val engines = listOf(
        "https://www.google.com/search?q=",
        "https://duckduckgo.com/html/?q=",
        "https://www.bing.com/search?q="
    )

    suspend fun startScraping(options: ScraperOptions, onLog: (String) -> Unit): Flow<ScrapedItem> = flow {
        val visited = mutableSetOf<String>()
        val startUrls = mutableListOf<String>()

        for (engineBase in engines) {
            val searchUrl = engineBase + options.query
            onLog("Attempting engine: $engineBase")
            val html = tryFetchHtml(searchUrl)
            if (html != null) {
                onLog("Success with $engineBase")
                val doc = Jsoup.parse(html)
                // Basic link extraction from search engines
                doc.select("a[href]").forEach { element ->
                    val link = element.absUrl("href")
                    if (isValidUrl(link) && !link.contains("google.com") && !link.contains("bing.com")) {
                        startUrls.add(link)
                    }
                }
                break // Success, don't need other engines for initial seeds
            } else {
                onLog("Blocked or failed on $engineBase, failing over...")
            }
        }

        // Recursive crawling
        for (url in startUrls) {
            crawlRecursive(url, 0, options.depth, options, visited, onLog).collect { emit(it) }
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun tryFetchHtml(url: String): String? {
        return try {
            val request = NetworkClient.getRequest(url)
            val response: Response = NetworkClient.client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else if (response.code == 403 || response.code == 429) {
                // Try WebView as failover for stealth
                webViewScraper.scrape(url)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun crawlRecursive(
        url: String,
        currentDepth: Int,
        maxDepth: Int,
        options: ScraperOptions,
        visited: MutableSetOf<String>,
        onLog: (String) -> Unit
    ): Flow<ScrapedItem> = flow {
        if (currentDepth > maxDepth || visited.contains(url)) return@flow
        visited.add(url)

        onLog("Crawling: $url (Depth: $currentDepth)")

        val html = tryFetchHtml(url) ?: return@flow
        val doc = Jsoup.parse(html, url)

        // API Discovery
        if (url.endsWith(".json") || html.contains("\"application/json\"")) {
             emit(ScrapedItem(url, ItemType.JSON, url))
        }

        // Image extraction
        if (options.scrapeImages) {
            doc.select("img[src]").forEach {
                val imgUrl = it.absUrl("src")
                if (imgUrl.isNotEmpty()) {
                    emit(ScrapedItem(imgUrl, ItemType.IMAGE, url))
                }
            }
        }

        // Recursive links
        if (currentDepth < maxDepth) {
            doc.select("a[href]").forEach {
                val link = it.absUrl("href")
                if (isValidUrl(link) && !visited.contains(link)) {
                    crawlRecursive(link, currentDepth + 1, maxDepth, options, visited, onLog).collect { emit(it) }
                }
            }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("http")
    }
}
