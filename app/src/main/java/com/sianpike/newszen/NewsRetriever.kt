package com.sianpike.newszen

import android.app.Activity
import com.beust.klaxon.Klaxon
import okhttp3.*
import okio.IOException

class NewsRetriever {

    private val client = OkHttpClient()

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
}