package com.sianpike.newszen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var topics: List<String>
    private val tag = "Topics"
    private lateinit var buttons: Array<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)

        if (intent.getStringExtra("userUID") != null) {

            userUID = intent.getStringExtra("userUID").toString()

        } else {

            userUID = FirebaseAuth.getInstance().currentUser?.uid!!
        }

        if (intent.getStringArrayExtra("topics") != null) {

            topics = (intent.getStringArrayExtra("topics") as Array<String>).toList()
        }

        businessButton = findViewById<Button>(R.id.businessButton)
        entertainmentButton = findViewById<Button>(R.id.entertainmentButton)
        generalButton = findViewById<Button>(R.id.generalButton)
        healthButton = findViewById<Button>(R.id.healthButton)
        scienceButton = findViewById<Button>(R.id.scienceButton)
        sportsButton = findViewById<Button>(R.id.sportsButton)
        technologyButton = findViewById<Button>(R.id.technologyButton)

        buttons = arrayOf<Button>(businessButton, entertainmentButton, generalButton,
                healthButton, scienceButton, sportsButton, technologyButton)

        for (button in buttons) {

            if (topics.contains(button.text.toString())) {

                button.isSelected = button.isSelected == false
            }
        }
    }

    private fun saveTopics(newTopics: Array<String>) {

        val data = hashMapOf(
            "topics" to newTopics.toList()
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

        var newTopics: Array<String> = emptyArray()

        for (button in buttons) {

            if (button.isSelected) {

                newTopics = appendArray(newTopics, button.text.toString())
            }
        }

        saveTopics(newTopics)

        val dashboard = Intent(this, Dashboard::class.java)
        dashboard.putExtra("topics", newTopics)
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