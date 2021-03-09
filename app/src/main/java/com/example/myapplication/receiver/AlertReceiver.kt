package com.example.myapplication.receiver

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.myapplication.data.local.database.WeatherDatabaseInstance
import com.example.myapplication.data.repositry.WeatherRepositiry
import com.example.myapplication.provider.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch{
            var database : WeatherRepositiry
            val notificationHelper = NotificationHelper(context,intent)
            val nc: NotificationCompat.Builder = notificationHelper.channelNotification
            notificationHelper.manager?.notify(1, nc.build())
            database= WeatherRepositiry(context.applicationContext as Application)
            var data =database.getAlertAlerm(context).descrption
            val eventAlert =intent.getStringExtra("event")
            if (intent!=null){
                data.contains(eventAlert!!)
            }
        }
    }
}