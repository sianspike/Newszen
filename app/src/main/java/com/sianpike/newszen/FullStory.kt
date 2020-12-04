package com.sianpike.newszen

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException


class FullStory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_story)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var extras = intent.extras
        var offline = extras?.get("offline") as Boolean
        val webView: WebView = findViewById(R.id.fullStoryWebsite)

        if (offline) {

            var cache = File(cacheDir, "webpagesOffline")

            webView.settings.apply {
                allowFileAccess = true
            }

            webView.loadUrl("file:///${cache.absolutePath}")

        } else {

            var url: String = extras?.get("url").toString()
            webView.loadUrl(url)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        this.finish();

        return super.onOptionsItemSelected(item)
    }
}