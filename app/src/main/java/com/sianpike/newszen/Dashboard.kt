package com.sianpike.newszen

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.RelativeLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.marginTop
import com.beust.klaxon.Klaxon
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import okhttp3.*
import okio.IOException
import java.io.InputStream
import java.net.URL


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

    fun loadImageFromURL(url: String?): Drawable? {

        return try {

            val inputStream: InputStream = URL(url).getContent() as InputStream

            Drawable.createFromStream(inputStream, "image")

        } catch (e: Exception) {

            null
        }
    }

    fun populateCards() {

        var imageViewOne = findViewById<ImageView>(R.id.imageOneView)
        var titleTextOne = findViewById<TextView>(R.id.titleOneText)
        var publisherTextOne = findViewById<TextView>(R.id.publisherOneText)
        var summaryTextOne = findViewById<TextView>(R.id.summaryOneText)
        var cardContainer = findViewById<RelativeLayout>(R.id.dashboardCardContainer)

        Picasso.get().load(articles[0].urlToImage).into(imageViewOne)
        titleTextOne.text = articles[0].title
        publisherTextOne.text = articles[0].source.name
        summaryTextOne.text = articles[0].description

        for (article in articles) {

            var card = CardView(this)
            val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 282)
            card.layoutParams = layoutParams
            cardContainer.addView(card)
        }
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
                        articles += result!!.articles

                        // Run view-related code back on the main thread
                        this@Dashboard.runOnUiThread {

                            try {

                                populateCards()

                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
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