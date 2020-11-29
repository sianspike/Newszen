package com.sianpike.newszen

import NewsAdapter
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


class NearYou() : Drawer(), LocationListener {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var recyclerView: RecyclerView
    private var tag = "Near You"
    private val client = OkHttpClient()
    private var currentLocation: Location? = null
    private val RECORD_REQUEST_CODE = 101
    private var locationManager: LocationManager? = null
    private var country: String? = ""

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
            retrieveNews(country)

        } else {

            setupPermissions()
        }
    }

    private fun getLocation(): String {

        val gcd = Geocoder(this, Locale.getDefault())
        var addresses = gcd.getFromLocation(
            currentLocation!!.latitude,
            currentLocation!!.latitude,
            1
        )

        return addresses[0].countryCode
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(tag, "Permission has been denied by user")

                } else {
                    Log.i(tag, "Permission has been granted by user")
                }
            }
        }
    }

    private fun setupPermissions() {

        Log.i(tag, "Permission to access location denied")

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )) {

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
            RECORD_REQUEST_CODE
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.options_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if (item.itemId == R.id.refresh) {

            retrieveNews(country)
            adapter!!.notifyDataSetChanged()
            var toast = Toast.makeText(applicationContext, "Refreshing...", Toast.LENGTH_SHORT)
            toast.show()

        }

        return true
    }

    private fun retrieveNews(country: String?) {

        for (topic in topics) {

            val request = Request.Builder()
                .url("https://newsapi.org/v2/top-headlines?country=$country&category=$topic")
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
                        this@NearYou.runOnUiThread {

                            try {

                                recyclerView.layoutManager = layoutManager

                                adapter = NewsAdapter(articles)
                                recyclerView.adapter = adapter
                                (adapter as NewsAdapter).filter.filter("")

                            } catch (e: IOException) {

                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
        }
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