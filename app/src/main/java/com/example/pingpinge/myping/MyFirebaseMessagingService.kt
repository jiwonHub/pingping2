package com.example.pingpinge.myping

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.pingpinge.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.RemoteMessage.Notification
import java.time.LocalDateTime

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "channel_id"
    private val NOTIFICATION_ID = 123

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"]
        val selectedDateTimeString = remoteMessage.data["selectedDateTime"]
        val selectedDateTime = LocalDateTime.parse(selectedDateTimeString)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("핑핑이 알림")
            .setContentText(title)
            .setSmallIcon(R.drawable.hi)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(CHANNEL_ID)
        }
        val notification = notificationBuilder.build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}