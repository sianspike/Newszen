package com.sianpike.newszen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class Topics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)
    }

    fun nextButtonClicked(view: View) {

        val dashboard = Intent(this, Dashboard::class.java)
        startActivity(dashboard)
    }

    fun businessButtonClicked(view: View) {

    }

    fun entertainmentButtonClicked(view: View) {

    }

    fun generalButtonClicked(view: View) {

    }

    fun healthButtonClicked(view: View) {

    }

    fun scienceButtonClicked(view: View) {

    }

    fun sportsButtonClicked(view: View) {

    }

    fun technologyButtonClicked(view: View) {

        var button = findViewById<Button>(R.id.technologyButton)

        button.isSelected = button.isSelected == false
    }
}