package com.example.myapplication.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.widget.Toast
import com.example.myapplication.view.activity.MainActivity
import java.util.*

class ContextUtils(base: Context) : ContextWrapper(base) {
    companion object {
        fun offlineToast(c: Context){
            Toast.makeText(c, "You are offline", Toast.LENGTH_LONG).show()
        }
        fun invalidAddressToast(c: Context){
            Toast.makeText(c, "Invalid Address", Toast.LENGTH_LONG).show()
        }
        fun setLocal(activity: Activity, langCode: String?) {
            val locale = Locale(langCode)
            Locale.setDefault(locale)
            val resources = activity.resources
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }

    }
}