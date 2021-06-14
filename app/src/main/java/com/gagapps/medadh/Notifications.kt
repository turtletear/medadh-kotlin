package com.gagapps.medadh

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat

class Notifications() {
    val NOTIFYTAG = "Medication Reminder"
    lateinit var notificationChannel: NotificationChannel
    private val channelId = "medadh"
    private val description = "Test notification"
    lateinit var builder: NotificationCompat.Builder

    fun notify(context: Context, msg: String){
        val intent = Intent(context, PatientDashboardActivity::class.java)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            this.builder = NotificationCompat.Builder(context, channelId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_medadh_notif)
                .setContentTitle(NOTIFYTAG)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT))
        } else {
            this.builder = NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_medadh_notif)
                .setContentTitle(NOTIFYTAG)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT))
        }

        notificationManager.notify(1234, builder.build())

    }
}