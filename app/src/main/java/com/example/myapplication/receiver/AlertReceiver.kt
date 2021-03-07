package com.example.myapplication.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.myapplication.provider.NotificationHelper

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val notificationHelper = NotificationHelper(context,intent)
        val nc: NotificationCompat.Builder = notificationHelper.channelNotification
        notificationHelper.manager?.notify(1, nc.build())

    }
}