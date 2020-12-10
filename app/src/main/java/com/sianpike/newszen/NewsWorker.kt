package com.sianpike.newszen

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.beust.klaxon.Klaxon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NewsWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private var topics: List<String> = emptyList()
    private val mAuth = FirebaseAuth.getInstance()
    private var notificationHelper: Notifications? =  Notifications(applicationContext)
    private var newArticles: Array<NewsArticle> = emptyArray()
    private var newArticlesTitle: Array<String> = emptyArray()
    private var gsonBuilder = GsonBuilder()
    private val topicsRetriever = TopicsRetriever()
    private val newsRetriever = NewsRetriever()
    private var articles: Array<String> = emptyArray()

    override suspend fun doWork(): Result {

        gsonBuilder.setLenient()
        val gson = gsonBuilder.create()
        val cache = File(applicationContext.cacheDir, "articles")

        topics = if (inputData.getStringArray("topics") == null) {

            topicsRetriever.retrieveTopics(mAuth.currentUser!!.uid)

        } else {

            (inputData.getStringArray("topics") as Array<String>).toList()
        }

        //retrieve news titles stored in cache.
        if (cache.exists() && cache.readText() != "") {

            articles = gson.fromJson(cache.readText(), Array<String>::class.java)
        }

        newArticles = newsRetriever.retrieveNewsNotAsync(topics)

        for (article in newArticles) {

            newArticlesTitle += article.title.toString()
        }

        cache.writeText(gson.toJson(newArticlesTitle))

        //Compare news retrieved immediately compared to news in cache.
        //If there is new articles, send notification.
        if (!articles.contentEquals(newArticlesTitle)) {

            println("New articles")
            postNotification(notification)
            return Result.success()
        }

        println("No new articles")
        return Result.failure()
    }

    //Post the notifications
    private fun postNotification(id: Int) {

        var notificationBuilder: NotificationCompat.Builder? = null

        when (id) {

            notification -> notificationBuilder = notificationHelper!!.getNotification()
        }

        if (notificationBuilder != null) {

            notificationHelper!!.notify(id, notificationBuilder)
        }
    }

    companion object {
        private const val notification = 101
    }
}