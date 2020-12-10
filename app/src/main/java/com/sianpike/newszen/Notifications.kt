package com.sianpike.newszen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import androidx.core.app.NotificationCompat

/**
 * Build notification.
 */
internal class Notifications (base: Context) : ContextWrapper(base) {

    private var notificationManager: NotificationManager? = null

    //Send your notifications to the NotificationManager system service//
    private val manager: NotificationManager?

        get() {

            if (notificationManager == null) {

                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            return notificationManager
        }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        val notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.setShowBadge(true)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        manager!!.createNotificationChannel(notificationChannel)
    }

    //Create the notification that’ll be posted to the Channel
    fun getNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle("Breaking News!")
                .setContentText("New stories available.")
                .setSmallIcon(R.drawable.notification)
                .setNumber(3)
                .setAutoCancel(true)
    }

    fun notify(id: Int, notification: NotificationCompat.Builder) {
        manager!!.notify(id, notification.build())
    }

    companion object {
        const val CHANNEL_ID = "com.sianpike.newszen.NEWSTORY"
        const val CHANNEL_NAME = "New Story"
    }
}