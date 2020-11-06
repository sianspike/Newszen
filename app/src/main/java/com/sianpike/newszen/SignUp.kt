package com.sianpike.newszen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val tag = "Sign Up"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    public override fun onStart() {
        super.onStart()

//        val dashboard = Intent(this, Dashboard::class.java)
//
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//
//        if (currentUser != null) {
//
//            startActivity(dashboard)
//            //updateUI(currentUser)
//        }

        Firebase.auth.signOut()
    }

    fun loginButtonClicked(view: View) {

        val login = Intent(this, Login::class.java)
        startActivity(login)
    }

    fun signUpButtonClicked(view: View) {

        var email = findViewById<TextView>(R.id.emailField).text.toString()
        var password = findViewById<TextView>(R.id.passwordField).text.toString()

        val topics = Intent(this, Topics::class.java)

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(tag, "createUserWithEmail:success")

                        val user = mAuth.currentUser

                        topics.putExtra("userUID", user!!.uid)
                        startActivity(topics)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(tag, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        //updateUI(null)
                    }
                }
    }
}