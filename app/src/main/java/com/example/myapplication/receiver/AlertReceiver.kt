package com.example.FinalProject2.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.myapplication.provider.NotificationHelper

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val notificationHelper =
            NotificationHelper(context,intent)
        val nb: NotificationCompat.Builder =
            notificationHelper.channelNotification
        notificationHelper.manager?.notify(1, nb.build())

    }
}