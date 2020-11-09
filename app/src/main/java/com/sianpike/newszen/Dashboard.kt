package com.sianpike.newszen

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.*
import okio.IOException


class Dashboard : AppCompatActivity() {

    private lateinit var topics: List<String>
    private var tag = "Dashboard"
    private val client = OkHttpClient()
    private val db = Firebase.firestore
    private var articles = listOf<NewsArticle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //Put menu icon to the left of the toolbar
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.menu);// set drawable icon
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        if (intent.getStringArrayExtra("topics") != null) {

            topics = (intent.getStringArrayExtra("topics") as Array<String>).toList()

            retrieveNews()

        } else {

            retrieveTopics(intent.getStringExtra("userUID")!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    private fun retrieveNews() {

        for (topic in topics) {

            val request = Request.Builder()
                    .url("https://newsapi.org/v2/top-headlines?country=gb&category=$topic")
                    .addHeader("x-api-key", "9952d1b8355247aba2d7dc3d260baec0")
                    .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {

                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {

                    response.use {

                        if (!response.isSuccessful) {

                            throw IOException("Unexpected code $response")
                        }

                        val result = Klaxon().parse<APIResult>(response.body!!.string())
                        articles = result!!.articles
                    }
                }
            })
        }
    }

    private fun retrieveTopics(user: String) {

        val currentUser = db.collection("users")
            .document("$user")

        currentUser.get()
            .addOnSuccessListener { document ->

                if (document != null) {

                    Log.d(tag, "DocumentSnapshot data: ${document.data}")

                    topics = document.get("topics") as List<String>

                    retrieveNews()

                } else {

                    Log.d(tag, "No such document")
                }

            }
            .addOnFailureListener { exception ->

                Log.d(tag, "get failed with ", exception)
            }
    }
}