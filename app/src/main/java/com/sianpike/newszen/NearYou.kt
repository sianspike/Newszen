package com.sianpike.newszen

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import okhttp3.*
import java.io.IOException
import java.util.*


class NearYou : Drawer(), LocationListener {

    private val recordRequestCode = 101
    private val tag = "Near You"
    private val newsRetriever = NewsRetriever()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var recyclerView: RecyclerView
    private var currentLocation: Location? = null
    private var locationManager: LocationManager? = null
    private var country: String? = ""

    /**
     * Set up recycler view.
     * Check user has given permission to access location.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_you)

        topics = (intent.getStringArrayExtra("topics") as Array<String>).toList()
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = NewsAdapter(articles)
        recyclerView.adapter = adapter
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

                if (currentLocation == null) {

                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000L,
                        1.0f,
                        this
                    )

                    if (locationManager != null) {

                        currentLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    }
                }

            country = getLocation()

            newsRetriever.retrieveNews(topics, this@NearYou, adapter as NewsAdapter, country)

        } else {

            setupPermissions()
        }
    }

    /**
     * Retrieve current stored location.
     */
    private fun getLocation(): String {

        val gcd = Geocoder(this, Locale.getDefault())
        val addresses = gcd.getFromLocation(
            currentLocation!!.latitude,
            currentLocation!!.latitude,
            1
        )

        return addresses[0].countryCode
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {

        when (requestCode) {

            recordRequestCode -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(tag, "Permission has been denied by user")

                } else {

                    Log.i(tag, "Permission has been granted by user")
                }
            }
        }
    }

    /**
     * Show dialog box to accept permissions if they have not yet been granted.
     */
    private fun setupPermissions() {

        Log.i(tag, "Permission to access location denied")

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

            val builder = AlertDialog.Builder(this)

            builder.setMessage("Permission to access location is required for this app to show news stories relevant to your location.")
                .setTitle("Permission required")

            builder.setPositiveButton("OK") { dialog, id ->

                Log.i(tag, "Clicked")
                makeRequest()
            }

            val dialog = builder.create()

            dialog.show()

        } else {

            makeRequest()
        }
    }

    private fun makeRequest() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            recordRequestCode
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.options_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if (item.itemId == R.id.refresh) {

            val toast = Toast.makeText(applicationContext, "Refreshing...", Toast.LENGTH_SHORT)

            toast.show()
            newsRetriever.retrieveNews(topics, this@NearYou, adapter as NewsAdapter, country)
            adapter?.notifyDataSetChanged()
        }

        return true
    }

    override fun onLocationChanged(location: Location) {

        try {

            val latitude: Double = location.latitude
            val longitude: Double = location.longitude

            Log.i("OK", "and$longitude$latitude")

        } catch (e: NullPointerException) {

        }
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }
}