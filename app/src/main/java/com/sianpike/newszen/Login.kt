package com.sianpike.newszen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private val tag = "Login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    public override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val dashboard = Intent(this, Dashboard::class.java)
        val currentUser = mAuth.currentUser

        if (currentUser != null) {

            dashboard.putExtra("userUID", currentUser.uid)
            startActivity(dashboard)
        }
    }

    fun signUpButtonClicked(view: View) {

        val signUp = Intent(this, SignUp::class.java)

        startActivity(signUp)
    }

    fun loginButtonClicked(view: View) {

        val email = findViewById<TextView>(R.id.emailField).text.toString()
        val password = findViewById<TextView>(R.id.passwordField).text.toString()
        val dashboard = Intent(this, Dashboard::class.java)

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithEmail:success")

                    val user = mAuth.currentUser!!

                    dashboard.putExtra("userUID", user.uid)
                    startActivity(dashboard)
                    finish()

                } else {

                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        moveTaskToBack(true)
    }
}