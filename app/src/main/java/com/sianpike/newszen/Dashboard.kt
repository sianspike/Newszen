package com.sianpike.newszen

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuItemCompat.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Dashboard : Drawer() {

    private val tag = "Dashboard"
    private val db = Firebase.firestore
    private val newsRetriever = NewsRetriever()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = NewsAdapter(articles)
        recyclerView.adapter = adapter

        if (intent.getStringArrayExtra("topics") != null) {

            topics = (intent.getStringArrayExtra("topics") as Array<String>).toList()

            newsRetriever.retrieveNews(topics, this@Dashboard, adapter as NewsAdapter, null)

        } else {

            retrieveTopics(intent.getStringExtra("userUID")!!)
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

    private fun retrieveTopics(user: String) {

        val currentUser = db.collection("users").document("$user")

        currentUser.get()
                .addOnSuccessListener { document ->

                if (document != null) {

                    Log.d(tag, "DocumentSnapshot data: ${document.data}")

                    topics = document.get("topics") as List<String>

                    newsRetriever.retrieveNews(topics, this@Dashboard, adapter as NewsAdapter, null)
                    adapter?.notifyDataSetChanged()

                } else {

                    Log.d(tag, "No such document")
                }

            }
            .addOnFailureListener { exception ->

                Log.d(tag, "get failed with ", exception)
            }
    }
}