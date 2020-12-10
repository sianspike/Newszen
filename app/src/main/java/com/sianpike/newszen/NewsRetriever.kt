package com.sianpike.newszen

import android.app.Activity
import com.beust.klaxon.Klaxon
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okio.IOException
import java.util.concurrent.CountDownLatch

class NewsRetriever {

    private val client = OkHttpClient()

    /**
     * Retrieve news articles based on topics and (optionally) country.
     */
    fun retrieveNews(topics: List<String>, activity: Activity, adapter: NewsAdapter, country: String?) {

        var countryUrl = ""

        if (country != null) {

            countryUrl = "&country=$country"
        }

        for (topic in topics) {

            val request = Request.Builder()
                    .url("https://newsapi.org/v2/top-headlines?category=$topic$countryUrl")
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
                        adapter.articles += result!!.articles

                        // Run view-related code back on the main thread
                        activity.runOnUiThread {

                            try {

                                adapter.filter.filter("")
                                adapter.notifyDataSetChanged()

                            } catch (e: java.io.IOException) {

                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
        }
    }

    /**
     * Retrieve news synchronously.
     */
    suspend fun retrieveNewsNotAsync(topics: List<String>): Array<NewsArticle> {

        var newArticles = emptyList<NewsArticle>()

        for (topic in topics) {

            val request = Request.Builder()
                    .url("https://newsapi.org/v2/top-headlines?category=$topic")
                    .addHeader("x-api-key", "9952d1b8355247aba2d7dc3d260baec0")
                    .build()

            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->

                    if (!response.isSuccessful) {

                        throw java.io.IOException("Unexpected code $response")
                    }

                    var result = Klaxon().parse<APIResult>(response.body!!.string())
                    newArticles += result!!.articles
                }
            }
        }

        return newArticles.toTypedArray()
    }
}