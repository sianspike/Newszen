package com.sianpike.newszen

import NewsAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class Drawer : AppCompatActivity() {

    var drawerLayout: DrawerLayout? = null
    var relativeLayout: RelativeLayout? = null
    lateinit var topics: List<String>
    var articles = listOf<NewsArticle>()

    var adapter: RecyclerView.Adapter<NewsAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        //Put menu icon to the left of the toolbar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
    }

    override fun setContentView(layoutResID: Int) {

        drawerLayout = layoutInflater.inflate(R.layout.activity_drawer, null) as DrawerLayout?
        relativeLayout = drawerLayout!!.findViewById(R.id.drawerFrame) as RelativeLayout
        layoutInflater.inflate(layoutResID, relativeLayout, true)
        super.setContentView(drawerLayout)
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
                (adapter as NewsAdapter).filter.filter(newText)
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {

            if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {

                drawerLayout!!.closeDrawer(GravityCompat.START)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.menu)

            } else {

                drawerLayout?.openDrawer(GravityCompat.START)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
            }

        } else if (item.itemId == R.id.refresh) {

//            Dashboard().retrieveNews()
//            adapter!!.notifyDataSetChanged()
//            var toast = Toast.makeText(applicationContext, "Refreshing...", Toast.LENGTH_SHORT)
//            toast.show()

        }

        return true
    }

    fun homeButtonClicked(view: View) {

        val dashboardIntent = Intent(this, Dashboard::class.java)
        dashboardIntent.putExtra("topics", topics.toTypedArray())
        startActivity(dashboardIntent)
    }

    fun nearYouButtonClicked(view: View) {

        val nearYouIntent = Intent(this, NearYou::class.java)
        nearYouIntent.putExtra("topics", topics.toTypedArray())
        startActivity(nearYouIntent)
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
}