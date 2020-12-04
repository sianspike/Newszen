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


class NewsAdapter(var articles: List<NewsArticle>) :
        RecyclerView.Adapter<NewsAdapter.ViewHolder>(), Filterable {

    var articleFilterList = ArrayList<NewsArticle>()
    var tag = "Adapter"
    lateinit var cache: File
    lateinit var webpageCache: File
    var jsonList = listOf<String>()
    var articleObject: String = ""
    val client = OkHttpClient()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemImage: ImageView = itemView.findViewById(R.id.imageView)
        var itemTitle: TextView = itemView.findViewById(R.id.titleText)
        var itemPublisher: TextView = itemView.findViewById(R.id.publisherText)
        var itemSummary: TextView = itemView.findViewById(R.id.summaryText)
        var download: Button = itemView.findViewById(R.id.downloadButton)

        init {

            cache = File(itemView.context.cacheDir, "downloadedStories")
            articleFilterList = articles as ArrayList<NewsArticle>
            var gson = Gson()

            if (cache.exists()) {

                try {

                    val listNewsArticleType = object : TypeToken<List<NewsArticle>>() {}.type
                    var article: ArrayList<NewsArticle> = gson.fromJson(cache.readText(), listNewsArticleType)
                    for (item in article) {

                        jsonList += gson.toJson(item)
                    }

                } catch (e: JsonSyntaxException) {

                    var article: NewsArticle = gson.fromJson(cache!!.readText(), NewsArticle::class.java)

                    jsonList += gson.toJson(article)
                }
            }

            itemView.setOnClickListener { v: View  ->

                var currentPosition: Int = adapterPosition
                val expandStory = Intent(v.context, FullStory::class.java)
                var offline: Boolean = false

                if (v.downloadButton.text == "Downloaded") {

                    offline = true
                }

                expandStory.putExtra("title", articleFilterList[currentPosition].title)
                expandStory.putExtra("publisher", articleFilterList[currentPosition].author)
                expandStory.putExtra("content", articleFilterList[currentPosition].content)
                expandStory.putExtra("image", articleFilterList[currentPosition].urlToImage)
                expandStory.putExtra("url", articleFilterList[currentPosition].url)
                expandStory.putExtra("offline", offline)
                expandStory.putExtra("webpageFile", articleFilterList[currentPosition].savedWebpage)
                v.context.startActivity(expandStory)
            }

            download.setOnClickListener { v: View ->

                //cache.delete()
                //webpageCache.delete()
                var currentPosition: Int = adapterPosition
                var source = Source(articleFilterList[currentPosition].source.id.toString(),
                        articles[currentPosition].source.name.toString())
                var author = articleFilterList[currentPosition].author.toString()
                var title = articleFilterList[currentPosition].title.toString()
                var description = articleFilterList[currentPosition].description.toString()
                var url = articleFilterList[currentPosition].url.toString()
                var urlToImage = articleFilterList[currentPosition].urlToImage.toString()
                var publishedAt = articleFilterList[currentPosition].publishedAt.toString()
                var content = articleFilterList[currentPosition].content.toString()
                var article = NewsArticle(source!!, author, title, description, url, urlToImage, publishedAt, content)
                val request = Request.Builder()
                        .url(url)
                        .build()
                webpageCache = File(itemView.context.cacheDir, "webpagesOffline")

                //create dictionary to point to correct html in cache
                client.newCall(request).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {

                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {

                        response.use {

                            if (!response.isSuccessful) {

                                throw IOException("Unexpected code $response")
                            }

                            var stream = response.body?.byteStream()
                            if (stream != null) {
                                webpageCache.writeText(stream.reader().readText())
                            }
                        }
                    }
                })

                var gson = Gson()
                articleObject = gson.toJson(article)

                Log.i(tag, "Download Button clicked!")

                if (jsonList.contains(articleObject)) {

                    download.text = itemView.context.getString(R.string.download)

                    for (item in jsonList) {

                        if (item == articleObject) {

                            jsonList -= articleObject
                            articleFilterList.remove(article)
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

            var gson = Gson()
            var temp = gson.fromJson(item, NewsArticle::class.java)

            if (articleFilterList.contains(temp)) {

                holder.download.text = holder.itemView.context.getString(R.string.downloaded)
            }
        }
    }

    override fun getItemCount(): Int {

        return articleFilterList.size
    }

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