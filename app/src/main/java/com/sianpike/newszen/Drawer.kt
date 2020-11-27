package com.sianpike.newszen

import DashboardAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class Drawer : AppCompatActivity() {

    var drawerLayout: DrawerLayout? = null
    var relativeLayout: RelativeLayout? = null
    lateinit var topics: List<String>

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {

            if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {

                drawerLayout!!.closeDrawer(GravityCompat.START)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.menu)

            } else {

                drawerLayout?.openDrawer(GravityCompat.START)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
            }

        }

        return true
    }

    fun nearYouButtonClicked(view: View) {

        val nearYouIntent = Intent(this, NearYou::class.java)
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