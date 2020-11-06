package com.sianpike.newszen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Topics : AppCompatActivity() {

    val db = Firebase.firestore
    var userUID = "Error"
    private lateinit var businessButton: Button
    private lateinit var entertainmentButton: Button
    private lateinit var generalButton: Button
    private lateinit var healthButton: Button
    private lateinit var scienceButton: Button
    private lateinit var sportsButton: Button
    private lateinit var technologyButton: Button
    private val tag = "Topics"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)

        userUID = intent.getStringExtra("userUID").toString()

        businessButton = findViewById<Button>(R.id.businessButton)
        entertainmentButton = findViewById<Button>(R.id.entertainmentButton)
        generalButton = findViewById<Button>(R.id.generalButton)
        healthButton = findViewById<Button>(R.id.healthButton)
        scienceButton = findViewById<Button>(R.id.scienceButton)
        sportsButton = findViewById<Button>(R.id.sportsButton)
        technologyButton = findViewById<Button>(R.id.technologyButton)
    }

    private fun saveTopics(topics: Array<String>) {

        val data = hashMapOf(
            "topics" to topics.toList()
        )

        db.collection("users").document(userUID!!).set(data, SetOptions.merge())
            .addOnSuccessListener {

                Log.d(tag, "Document snapshot successfully written!")
            }
            .addOnFailureListener { error ->

                Log.w(tag, "Error writing document.", error)
            }
    }

    private fun appendArray(array: Array<String>, element: String): Array<String> {

        val list: MutableList<String> = array.toMutableList()
        list.add(element)

        return list.toTypedArray()
    }

    fun nextButtonClicked(view: View) {

        var topics: Array<String> = emptyArray()
        var buttons = arrayOf<Button>(businessButton, entertainmentButton, generalButton,
            healthButton, scienceButton, sportsButton, technologyButton)

        for (button in buttons) {

            if (button.isSelected) {

                topics = appendArray(topics, button.text.toString())
            }
        }

        saveTopics(topics)

        val dashboard = Intent(this, Dashboard::class.java)
        dashboard.putExtra("topics", topics)
        startActivity(dashboard)
    }

    fun businessButtonClicked(view: View) {

        businessButton.isSelected = businessButton.isSelected == false
    }

    fun entertainmentButtonClicked(view: View) {

        entertainmentButton.isSelected = entertainmentButton.isSelected == false
    }

    fun generalButtonClicked(view: View) {

        generalButton.isSelected = generalButton.isSelected == false
    }

    fun healthButtonClicked(view: View) {

        healthButton.isSelected = healthButton.isSelected == false
    }

    fun scienceButtonClicked(view: View) {

        scienceButton.isSelected = scienceButton.isSelected == false
    }

    fun sportsButtonClicked(view: View) {

        sportsButton.isSelected = sportsButton.isSelected == false
    }

    fun technologyButtonClicked(view: View) {

        technologyButton.isSelected = technologyButton.isSelected == false
    }
}