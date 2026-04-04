# DorkScraper - Native Android Scraper

DorkScraper is a powerful, native Android application built with Jetpack Compose that orchestrates search engine dorking and recursive web crawling. It features a multi-engine waterfall approach and stealth capabilities to bypass blocks.

## Features

- **Multi-Engine Waterfall:** Automatically fails over from **Google -> DuckDuckGo -> Bing** if blocked (403/429 errors).
- **Recursive Depth Crawling:** Recursively crawls `<a>` and `<img>` tags up to a user-defined depth (0-5 levels).
- **Stealth Mode:** Rotates User-Agent strings and uses a hidden WebView failover for JS-heavy or protected sites.
- **API Discovery:** Automatically identifies and lists URLs returning `application/json` or ending in `.json`.
- **Jetpack Compose UI:**
    - Real-time console log for engine and recursion progress.
    - 3-column responsive image gallery.
    - Full-screen image viewer with "Download to Device" functionality.
- **Modern Tech Stack:** Built with Kotlin, Coroutines, OkHttp, Jsoup, Coil, and Jetpack Compose.

## Project Structure

```
com.app.dorkscraper
├── data
│   ├── model       # Data models (ScrapedItem, ScraperOptions)
│   ├── remote      # Networking, SearchOrchestrator, WebViewScraper
│   └── repository  # ScraperRepository
├── ui
│   ├── components  # Compose UI components (Gallery, Form, Console)
│   └── viewmodel   # ScraperViewModel
├── util            # DownloadHelper and utilities
└── MainActivity.kt # Main entry point
```

## Setup and Requirements

- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34+
- **Permissions:** Internet, Read Media Images, Write External Storage (for legacy downloads).

To build the project:
1. Open in Android Studio.
2. Sync Gradle dependencies.
3. Run on an emulator or physical device.

## License

This project is for educational and research purposes. Use responsibly.
