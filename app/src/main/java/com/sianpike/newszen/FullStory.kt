package com.sianpike.newszen

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.File

class FullStory : AppCompatActivity() {

    /**
     * If app is online, render website with url.
     * If app is offline, get html and render text only view with cache.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_story)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        val offline = extras?.get("offline") as Boolean
        val author = extras?.get("publisher")
        val title = extras?.get("title")
        val webView: WebView = findViewById(R.id.fullStoryWebsite)
        val gson = Gson()

        if (offline) {

            val cache = File(cacheDir, "webpagesOffline")
            val read = cache.readText()
            val map: Map<String, String> = gson.fromJson(read, Map::class.java) as Map<String, String>
            val file = map["$author$title"]

            cache.writeText(file!!)
            webView.settings.apply {

                allowFileAccess = true
            }

            webView.loadUrl("file:///${cache.absolutePath}")

            val converted = gson.toJson(map)

            cache.writeText(converted)

        } else {

            val url: String = extras?.get("url").toString()

            webView.loadUrl(url)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        this.finish();
        return super.onOptionsItemSelected(item)
    }
}