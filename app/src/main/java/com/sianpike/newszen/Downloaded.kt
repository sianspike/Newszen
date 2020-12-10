package com.sianpike.newszen

import android.os.Bundle
import android.view.Menu
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

        //Initialise recycler view
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = NewsAdapter(articles)
        recyclerView.adapter = adapter

        if (intent.getStringArrayExtra("topics") != null) {

            topics = (intent.getStringArrayExtra("topics") as Array<String>).toList()
        }

        getCache()
        display()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        val refreshIcon = menu.findItem(R.id.refresh)

        menu.removeItem(refreshIcon.itemId)

        return true
    }

    /**
     * Retrieve downloaded news articles from cache.
     */
    private fun getCache() {

        val gsonBuilder = GsonBuilder()
        val cache = File(applicationContext.cacheDir, "downloadedStories")

        gsonBuilder.setLenient()

        if (cache.exists()) {

            val gson = gsonBuilder.create()

            articles = try {

                val listNewsArticleType = object : TypeToken<List<NewsArticle>>() {}.type
                val article: ArrayList<NewsArticle> = gson.fromJson(cache!!.readText(), listNewsArticleType)
                article

            } catch (e: JsonSyntaxException) {

                val article: NewsArticle = gson.fromJson(cache!!.readText(), NewsArticle::class.java)
                arrayListOf(article)
            }

        }
    }

    /**
     * Refresh recycler view with downloaded news stories.
     */
    private fun display() {

        recyclerView.layoutManager = layoutManager
        adapter = NewsAdapter(articles)
        recyclerView.adapter = adapter

        (adapter as NewsAdapter).filter.filter("")
    }
}