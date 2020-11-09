package com.sianpike.newszen

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Firestore {

//    private var mAuth = FirebaseAuth.getInstance()
//    private val tag = "Firestore"
//
//    fun createUser(email: String, password: String): FirebaseUser? {
//
//        var user: FirebaseUser? = null
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(tag, "createUserWithEmail:success")
//
//                    val user = mAuth.currentUser
//
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(tag, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    //updateUI(null)
//                }
//            }
//
//        return user
//    }
}