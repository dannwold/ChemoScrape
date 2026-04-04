package com.app.dorkscraper.data.model

data class ScrapedItem(
    val url: String,
    val type: ItemType,
    val sourceUrl: String
)

enum class ItemType {
    IMAGE, JSON, LINK, VIDEO
}

data class ScraperOptions(
    val query: String,
    val depth: Int,
    val scrapeImages: Boolean,
    val scrapeVideos: Boolean,
    val scrapeJson: Boolean
)
