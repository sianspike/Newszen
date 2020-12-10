package com.sianpike.newszen

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Retrieve topucs from firebase.
 */
class TopicsRetriever {

    private val db = Firebase.firestore
    private val tag = "Topics Retriever"
    private val newsRetriever = NewsRetriever()

    fun retrieveTopics(user: String): List<String> {

        val currentUser = db.collection("users").document(user)
        var topics = emptyList<String>()

        currentUser.get()
                .addOnSuccessListener { document ->

                    if (document != null) {

                        Log.d(tag, "DocumentSnapshot data: ${document.data}")

                        topics = document.get("topics") as List<String>
                    }
                }
                .addOnFailureListener { exception ->

                    Log.d(tag, "get failed with ", exception)
                    exception.printStackTrace()
                }

        return topics
    }
}