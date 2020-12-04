package com.sianpike.newszen

import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File

class Downloaded : Drawer(){

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloaded)

        if (intent.getStringArrayExtra("topics") != null) {

            topics = (intent.getStringArrayExtra("topics") as Array<String>).toList()
        }

        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = NewsAdapter(articles)
        recyclerView.adapter = adapter

        getCache()
        display()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        val refreshIcon = menu.findItem(R.id.refresh)
        menu.removeItem(refreshIcon.itemId)

        return true
    }

    private fun getCache() {

        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        var cache = File(applicationContext.cacheDir, "downloadedStories")

        if (cache.exists()) {
            println(cache.readText())
            var gson = gsonBuilder.create()

            articles = try {

                val listNewsArticleType = object : TypeToken<List<NewsArticle>>() {}.type
                var article: ArrayList<NewsArticle> = gson.fromJson(cache!!.readText(), listNewsArticleType)
                article

            } catch (e: JsonSyntaxException) {

                var article: NewsArticle = gson.fromJson(cache!!.readText(), NewsArticle::class.java)
                arrayListOf(article)
            }
        } else {

            //add text
        }
    }

    private fun display() {

        recyclerView.layoutManager = layoutManager
        adapter = NewsAdapter(articles)
        recyclerView.adapter = adapter
        (adapter as NewsAdapter).filter.filter("")
    }
}