import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sianpike.newszen.FullStory
import com.sianpike.newszen.NewsArticle
import com.sianpike.newszen.R
import com.squareup.picasso.Picasso


class DashboardAdapter(var articles: List<NewsArticle>) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

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

        Picasso.get().load(articles[position].urlToImage).into(holder.itemImage)
        holder.itemTitle.text = articles[position].title
        holder.itemPublisher.text = articles[position].author
        holder.itemSummary.text = articles[position].description
    }

    override fun getItemCount(): Int {

        return articles.size
    }
}