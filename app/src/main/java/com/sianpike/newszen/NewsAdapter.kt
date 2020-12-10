package com.sianpike.newszen
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.squareup.okhttp.HttpUrl
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_card.view.*
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

/**
 * Recyler view adapter.
 */
class NewsAdapter(var articles: List<NewsArticle>) :
        RecyclerView.Adapter<NewsAdapter.ViewHolder>(), Filterable {

    private val tag = "Adapter"
    private val client = OkHttpClient()
    private var articleFilterList = ArrayList<NewsArticle>()
    private lateinit var cache: File
    private lateinit var webpageCache: File
    private var jsonList = listOf<String>()
    private var articleObject: String = ""
    private var map = mutableMapOf<String, String>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemImage: ImageView = itemView.findViewById(R.id.imageView)
        val itemTitle: TextView = itemView.findViewById(R.id.titleText)
        val itemPublisher: TextView = itemView.findViewById(R.id.publisherText)
        val itemSummary: TextView = itemView.findViewById(R.id.summaryText)
        val download: Button = itemView.findViewById(R.id.downloadButton)

        init {

            val gson = Gson()
            cache = File(itemView.context.cacheDir, "downloadedStories")
            webpageCache = File(itemView.context.cacheDir, "webpagesOffline")
            articleFilterList = articles as ArrayList<NewsArticle>

            //Retrieve downloaded news stories from cache.
            if (cache.exists()) {

                try {

                    val listNewsArticleType = object : TypeToken<List<NewsArticle>>() {}.type
                    val article: ArrayList<NewsArticle> = gson.fromJson(cache.readText(), listNewsArticleType)

                    for (item in article) {

                        jsonList += gson.toJson(item)
                    }

                } catch (e: JsonSyntaxException) {

                    val article: NewsArticle = gson.fromJson(cache!!.readText(), NewsArticle::class.java)

                    jsonList += gson.toJson(article)
                }
            }

            //When story is clicked, open full story.
            itemView.setOnClickListener { v: View  ->

                val currentPosition: Int = adapterPosition
                val expandStory = Intent(v.context, FullStory::class.java)
                var offline: Boolean = false

                if (v.downloadButton.text == "Downloaded") {

                    offline = true
                }

                expandStory.putExtra("title", articleFilterList[currentPosition].title)
                expandStory.putExtra("publisher", articleFilterList[currentPosition].author)
                expandStory.putExtra("url", articleFilterList[currentPosition].url)
                expandStory.putExtra("offline", offline)
                v.context.startActivity(expandStory)
            }

            //When download is clicked, download story to cache.
            download.setOnClickListener { v: View ->

                //cache.delete()
                //webpageCache.delete()
                Log.i(tag, "Download Button clicked!")

                val currentPosition: Int = adapterPosition
                val source = Source(articleFilterList[currentPosition].source.id.toString(),
                        articles[currentPosition].source.name.toString())
                val author = articleFilterList[currentPosition].author.toString()
                val title = articleFilterList[currentPosition].title.toString()
                val description = articleFilterList[currentPosition].description.toString()
                val url = articleFilterList[currentPosition].url.toString()
                val urlToImage = articleFilterList[currentPosition].urlToImage.toString()
                val publishedAt = articleFilterList[currentPosition].publishedAt.toString()
                val content = articleFilterList[currentPosition].content.toString()
                val article = NewsArticle(source!!, author, title, description, url, urlToImage, publishedAt, content)
                val request = Request.Builder()
                        .url(url)
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

                            val stream = response.body?.byteStream()

                            if (stream != null) {

                                map["$author$title"] = stream.reader().readText()
                            }

                            val convertedToJson = gson.toJson(map)

                            webpageCache.writeText(convertedToJson)
                        }
                    }
                })

                val gson = Gson()

                articleObject = gson.toJson(article)

                if (jsonList.contains(articleObject)) {

                    download.text = itemView.context.getString(R.string.download)

                    for (item in jsonList) {

                        if (item == articleObject) {

                            jsonList -= articleObject

                            articleFilterList.remove(article)
                            map.remove("$author$title")
                            notifyItemRemoved(currentPosition)
                        }
                    }

                } else {

                    download.text = itemView.context.getString(R.string.downloaded)
                    jsonList += articleObject
                }

                cache.writeText(jsonList.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.news_card, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get().load(articleFilterList[position].urlToImage).into(holder.itemImage)
        holder.itemTitle.text = articleFilterList[position].title
        holder.itemPublisher.text = articleFilterList[position].author
        holder.itemSummary.text = articleFilterList[position].description

        for (item in jsonList) {

            val gson = Gson()
            val tempItem = gson.fromJson(item, NewsArticle::class.java)

            if (articleFilterList.contains(tempItem)) {

                holder.download.text = holder.itemView.context.getString(R.string.downloaded)
            }
        }
    }

    override fun getItemCount(): Int {

        return articleFilterList.size
    }

    //Filter stories from search bar.
    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()

                if (charSearch.isNotEmpty()) {

                    val resultList = ArrayList<NewsArticle>()

                    for (row in articles as ArrayList<NewsArticle>) {

                        if (row.title?.toLowerCase(Locale.ROOT)?.contains(charSearch.toLowerCase(Locale.ROOT)) == true ||
                                row.author?.toLowerCase(Locale.ROOT)?.contains(charSearch.toLowerCase(Locale.ROOT)) == true ||
                                row.source.name?.toLowerCase(Locale.ROOT)?.contains(charSearch.toLowerCase(Locale.ROOT)) == true) {

                            resultList.add(row)
                        }
                    }

                    articleFilterList = resultList

                } else {

                    articleFilterList = articles as ArrayList<NewsArticle>
                }

                val filterResults = FilterResults()
                filterResults.values = articleFilterList

                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                articleFilterList = results?.values as ArrayList<NewsArticle>

                notifyDataSetChanged()
            }
        }
    }
}