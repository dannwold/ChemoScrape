package com.app.dorkscraper.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class WebViewScraper(private val context: Context) {

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun scrape(url: String): String = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val webView = WebView(context)

            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.evaluateJavascript("(function() { return document.documentElement.outerHTML; })();") { html ->
                        // html is returned as a JSON string
                        val cleanHtml = html?.replace("\\u003C", "<")
                            ?.replace("\\\"", "\"")
                            ?.removePrefix("\"")
                            ?.removeSuffix("\"")
                        continuation.resume(cleanHtml ?: "")
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return false
                }
            }

            webView.loadUrl(url)
        }
    }
}
