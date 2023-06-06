package com.example.pingpinge.myping

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var notificationHelper: NotificationHelper = NotificationHelper(context)
        var title = intent?.extras?.getString("title")
        var nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(title)

        notificationHelper.getManager().notify(1, nb.build())
    }
}