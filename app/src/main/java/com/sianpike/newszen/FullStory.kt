package com.sianpike.newszen

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

class FullStory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_story)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        var url: String = intent.getStringExtra("url").toString()
        val webView: WebView = findViewById<WebView>(R.id.fullStoryWebsite)

        webView.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        this.finish();

        return super.onOptionsItemSelected(item)
    }
}