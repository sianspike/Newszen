package com.sianpike.newszen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.work.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Toggle notifications to recieve notifications about. Update firebase with topic changes.
 */
class NotificationSettings : AppCompatActivity() {

    var topics = emptyList<String>()
    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()
    private val tag = "Notification Settings"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        //Put menu icon to the left of the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        if (intent.getStringArrayExtra("topics") != null) {

            topics = (intent.getStringArrayExtra("topics") as Array<String>).toList()
        }

        val businessSwitch = findViewById<SwitchCompat>(R.id.businessSwitch)
        val entertainmentSwitch = findViewById<SwitchCompat>(R.id.entertainmentSwitch)
        val generalSwitch = findViewById<SwitchCompat>(R.id.generalSwitch)
        val healthSwitch = findViewById<SwitchCompat>(R.id.healthSwitch)
        val scienceSwitch = findViewById<SwitchCompat>(R.id.scienceSwitch)
        val sportsSwitch = findViewById<SwitchCompat>(R.id.sportsSwitch)
        val technologySwitch = findViewById<SwitchCompat>(R.id.technologySwitch)
        val switches: Array<SwitchCompat> = arrayOf(businessSwitch, entertainmentSwitch, generalSwitch,
            healthSwitch, scienceSwitch, sportsSwitch, technologySwitch)

        for (switch in switches) {

            for (topic in topics) {

                if (switch.text == topic) {

                    switch.isEnabled = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.options_menu, menu)

        val refreshIcon = menu.findItem(R.id.refresh)
        val searchIcon = menu.findItem(R.id.search)

        menu.removeItem(refreshIcon.itemId)
        menu.removeItem(searchIcon.itemId)
        return true
    }

    private fun saveTopics(newTopics: Array<String>) {

        val data = hashMapOf("topics" to newTopics.toList())

        mAuth.currentUser?.let {
            db.collection("users").document(it.uid).set(data, SetOptions.merge())
                .addOnSuccessListener {

                    Log.d(tag, "Document snapshot successfully written!")
                }
                .addOnFailureListener { error ->

                    Log.w(tag, "Error writing document.", error)
                }
        }
    }

    fun saveButtonClicked(view: View) {

        saveTopics(topics.toTypedArray())
    }

    fun businessSwitchTapped(view: View) {

    }

    fun entertainmentSwitchTapped(view: View) {

    }

    fun generalSwitchTapped(view: View) {

    }

    fun healthSwitchTapped(view: View) {

    }

    fun scienceSwitchTapped(view: View) {

    }

    fun sportsSwitchTapped(view: View) {

    }

    fun technologySwitchTapped(view: View) {

    }
}