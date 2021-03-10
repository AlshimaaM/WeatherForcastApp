package com.example.myapplication.util

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.example.myapplication.R
import com.example.myapplication.receiver.AlertReceiver
import com.victor.loading.rotate.RotateLoading

object Dialogs {
    fun createProgressBarDialog(
        mContext: Context?,
        text: String?
    ): Dialog {
        val mDialog: Dialog
        mDialog = Dialog(mContext!!, R.style.Theme_Dialog)
        mDialog.setContentView(R.layout.dialog_progress_bar)

        //find Views
        val rotateLoading: RotateLoading =
            mDialog.findViewById<View>(R.id.rotateloading) as RotateLoading
        rotateLoading.start()
        mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.setCancelable(false)
        return mDialog
    }
    fun cancelAlarm(context:Context,requestCode: Int) {
        val intent = Intent(context, AlertReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        val alarmManager =context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }
}