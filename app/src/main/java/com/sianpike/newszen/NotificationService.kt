package com.sianpike.newszen

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationService: Service() {

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    /**
     * Check for new news articles every hour.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val topics = (intent?.getStringArrayExtra("topics") as Array<String>).toList()
        val data = Data.Builder().putStringArray("topics", topics.toTypedArray()).build()

        val workRequest: WorkRequest =
            PeriodicWorkRequestBuilder<NewsWorker>(1, TimeUnit.HOURS)
                    .setInitialDelay(2, TimeUnit.MINUTES)
                    .setInputData(data)
                    .build()
        var workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(workRequest)

        return START_STICKY
    }
}