package com.example.FinalProject2.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myapplication.view.activity.DialogActivity

class DialogReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, DialogActivity::class.java)
        i.putExtra("event", intent.getStringExtra("event"))
        i.putExtra("desc", intent.getStringExtra("desc"))
       i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)

    }
}