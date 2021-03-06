package com.example.myapplication.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.widget.Toast
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
        fun updateLocalization(c: Context, localeToSwitchTo: Locale): ContextWrapper {
            var context = c
            val resources: Resources = context.resources
            val configuration: Configuration = resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeList = LocaleList(localeToSwitchTo)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            } else {
                configuration.locale = localeToSwitchTo
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                context = context.createConfigurationContext(configuration)
            } else {
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }
            return ContextUtils(context)
        }


    }
}