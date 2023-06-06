package com.example.pingpinge.myping

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.pingpinge.R

class NotificationHelper(base: Context?): ContextWrapper(base) {

    private val channelId = "channelId"
    private val channelNm = "channelNm"

    init {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(){
        var channel = NotificationChannel(channelId, channelNm,
        NotificationManager.IMPORTANCE_DEFAULT)

        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = Color.GREEN
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager{
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    fun getChannelNotification(title: String?): NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("핑핑이 알림")
            .setContentText(title)
            .setSmallIcon(R.drawable.hi)
    }

}