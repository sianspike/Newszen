import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sianpike.newszen.FullStory
import com.sianpike.newszen.NewsArticle
import com.sianpike.newszen.R
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


class DashboardAdapter(var articles: List<NewsArticle>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>(), Filterable {

    var articleFilterList = ArrayList<NewsArticle>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemImage: ImageView
        var itemTitle: TextView
        var itemPublisher: TextView
        var itemSummary: TextView

        init {

            itemImage = itemView.findViewById(R.id.imageView)
            itemTitle = itemView.findViewById(R.id.titleText)
            itemPublisher = itemView.findViewById(R.id.publisherText)
            itemSummary = itemView.findViewById(R.id.summaryText)
            articleFilterList = articles as ArrayList<NewsArticle>

            itemView.setOnClickListener { v: View  ->

                var position: Int = adapterPosition

                val expandStory = Intent(v.context, FullStory::class.java)
                expandStory.putExtra("title", articles[position].title)
                expandStory.putExtra("publisher", articles[position].author)
                expandStory.putExtra("content", articles[position].content)
                expandStory.putExtra("image", articles[position].urlToImage)
                expandStory.putExtra("url", articles[position].url)
                v.context.startActivity(expandStory)
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
    }

    override fun getItemCount(): Int {

        return articleFilterList.size
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()

                if (!charSearch.isEmpty()) {

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