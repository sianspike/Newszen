package com.sianpike.newszen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUp : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        var currentUser: FirebaseUser = mAuth.getCurrentUser()
        updateUI(currentUser)
    }

    fun loginButtonClicked(view: View) {

        val login = Intent(this, Login::class.java)
        startActivity(login)
    }

    fun signUpButtonClicked(view: View) {

        val topics = Intent(this, Topics::class.java)
        startActivity(topics)
    }
}