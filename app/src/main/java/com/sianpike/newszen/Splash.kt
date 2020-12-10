package com.sianpike.newszen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.internal.wait
import java.io.File
import java.util.*
import kotlin.concurrent.schedule

/**
 * Show splash screen and set up cache locations.
 */
class Splash : AppCompatActivity() {

    private val waitingTime: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        File.createTempFile("downloadedStories", null, applicationContext.cacheDir)
        File.createTempFile("webpagesOffline", null, applicationContext.cacheDir)
        File.createTempFile("articles", null, applicationContext.cacheDir)

        val login = Intent(this, Login::class.java)

        Timer().schedule(waitingTime) {

            startActivity(login)
        }
    }
}