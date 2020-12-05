package com.sianpike.newszen

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.File
import java.io.IOException


class FullStory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_story)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var extras = intent.extras
        var offline = extras?.get("offline") as Boolean
        var author = extras?.get("publisher")
        var title = extras?.get("title")
        val webView: WebView = findViewById(R.id.fullStoryWebsite)
        var gson = Gson()

        if (offline) {

            var cache = File(cacheDir, "webpagesOffline")
            var read = cache.readText()
            var map: Map<String, String> = gson.fromJson(read, Map::class.java) as Map<String, String>
            var file = map["$author$title"]
            cache.writeText(file!!)

            webView.settings.apply {
                allowFileAccess = true
            }

            webView.loadUrl("file:///${cache.absolutePath}")

            var converted = gson.toJson(map)
            cache.writeText(converted)

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