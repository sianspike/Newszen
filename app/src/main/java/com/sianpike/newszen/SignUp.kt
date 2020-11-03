package com.sianpike.newszen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
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