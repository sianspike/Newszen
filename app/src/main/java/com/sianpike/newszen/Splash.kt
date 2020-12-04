package com.sianpike.newszen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.File
import java.util.*
import kotlin.concurrent.schedule

class Splash : AppCompatActivity() {

    private val waitingTime: Int = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        File.createTempFile("downloadedStories", null, applicationContext.cacheDir)
        File.createTempFile("webpagesOffline", null, applicationContext.cacheDir)

        val login = Intent(this, Login::class.java)

        Timer().schedule(3000) {

            startActivity(login)
        }
    }
}