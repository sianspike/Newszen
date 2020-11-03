package com.sianpike.newszen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun signUpButtonClicked(view: View) {

        val signUp = Intent(this, SignUp::class.java)
        startActivity(signUp)
    }

    fun loginButtonClicked(view: View) {

        val dashboard = Intent(this, Dashboard::class.java)
        startActivity(dashboard)
    }
}