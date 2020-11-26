package com.sianpike.newszen

import DashboardAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.MenuItemCompat.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.*
import okio.IOException


class Dashboard : AppCompatActivity() {

    private lateinit var topics: List<String>
    private var tag = "Dashboard"
    private val client = OkHttpClient()
    private val db = Firebase.firestore
    var articles = listOf<NewsArticle>()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<DashboardAdapter.ViewHolder>? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawerLayout)

        //Put menu icon to the left of the toolbar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        if (intent.getStringArrayExtra("topics") != null) {

            topics = (intent.getStringArrayExtra("topics") as Array<String>).toList()

            retrieveNews()

        } else {

            retrieveTopics(intent.getStringExtra("userUID")!!)
        }

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = DashboardAdapter(articles)
        recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                drawerLayout.closeDrawer(GravityCompat.START)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.menu)

            } else {

                drawerLayout.openDrawer(GravityCompat.START)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
            }

        } else if (item.itemId == R.id.refresh) {

            retrieveNews()
            adapter!!.notifyDataSetChanged()
            var toast = Toast.makeText(applicationContext, "Refreshing...", Toast.LENGTH_SHORT)
            toast.show()

        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchView: SearchView = MenuItemCompat.getActionView(searchItem) as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (adapter as DashboardAdapter).filter.filter(newText)
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    fun nearYouButtonClicked(view: View) {


    }

    fun topicsButtonClicked(view: View) {

        val topicsIntent = Intent(this, Topics::class.java)
        topicsIntent.putExtra("topics", topics.toTypedArray())
        startActivity(topicsIntent)
    }

    fun downloadedButtonClicked(view: View) {

    }

    fun notificationsButtonClicked(view: View) {

    }

    fun logoutButtonClicked(view: View) {

        Firebase.auth.signOut()
        val login = Intent(this, Login::class.java)
        startActivity(login)
        finish()
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

                                recyclerView.layoutManager = layoutManager

                                adapter = DashboardAdapter(articles)
                                recyclerView.adapter = adapter
                                (adapter as DashboardAdapter).filter.filter("")

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