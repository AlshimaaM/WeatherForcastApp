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
import androidx.preference.PreferenceManager
import com.example.myapplication.provider.Setting
import java.util.*

class ContextUtils(base: Context) : ContextWrapper(base) {

    companion object {
        fun offlineToast(c: Context) {
            Toast.makeText(c, "You are offline", Toast.LENGTH_LONG).show()
        }

        fun invalidAddressToast(c: Context) {
            Toast.makeText(c, "Invalid Address", Toast.LENGTH_LONG).show()
        }

         fun setLocale(activity: Activity, languageCode: String?) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val resources: Resources = activity.resources
            val config: Configuration = resources.configuration
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

        fun settings(context: Context) {
            var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            var editor = sharedPreferences.edit()
            val unitSystem = sharedPreferences.getString("UNIT_SYSTEM", "")
            val languageSystem = sharedPreferences.getString("LANGUAGE_SYSTEM", "")
            val location1 = sharedPreferences.getBoolean("USE_DEVICE_LOCATION", false)
            val locations = sharedPreferences.getString("CUSTOM_LOCATION", "")
            val mapLocation = sharedPreferences.getBoolean("MAP_LOCATION", false)

            if (unitSystem != null) {
                Setting.unitSystem = unitSystem
            }
            if (languageSystem != null) {
                Setting.languageSystem = languageSystem
            }
            if (location1 != null) {
                Setting.deviceLocation = location1
            }
            if (locations != null) {
                Setting.customLocations = locations
            }
            Setting.mapLocation = mapLocation!!
        }
    }
}