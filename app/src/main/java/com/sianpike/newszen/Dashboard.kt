package com.sianpike.newszen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuItemCompat.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Dashboard : Drawer() {

    private val newsRetriever = NewsRetriever()
    private val topicsRetriever = TopicsRetriever()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        serviceIntent = Intent(this, NotificationService::class.java)

        //Initialise the recycler view
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = NewsAdapter(articles)
        recyclerView.adapter = adapter

        //Get user topics and pass them to the notification service and news retriever
        if (intent.getStringArrayExtra("topics") != null) {

            topics = (intent.getStringArrayExtra("topics") as Array<String>).toList()

            serviceIntent.putExtra("topics", topics.toTypedArray())
            startService(serviceIntent)
            newsRetriever.retrieveNews(topics, this@Dashboard, adapter as NewsAdapter, null)

        } else {

            topics = topicsRetriever.retrieveTopics(intent.getStringExtra("userUID")!!)

            serviceIntent.putExtra("topics", topics.toTypedArray())
            startService(serviceIntent)
            newsRetriever.retrieveNews(topics, this@Dashboard, adapter as NewsAdapter, null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if (item.itemId == R.id.refresh) {

            val toast = Toast.makeText(applicationContext, "Refreshing...", Toast.LENGTH_SHORT)

            toast.show()
            newsRetriever.retrieveNews(topics, this@Dashboard, adapter as NewsAdapter, null)
        }

        return true
    }
}