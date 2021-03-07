package com.example.myapplication.provider

import android.content.Context
import android.content.SharedPreferences

object Setting {
        var unitSystem:String=""
        var languageSystem:String=""
        var deviceLocation:Boolean=false
        var customLocations:String=""
        var latitude:String=""
        var longitude:String=""
        var notifications:Boolean=false
        var mapLatitude:String =""
        var mapLongitude:String=""
        var mapLocation:Boolean=false

        private lateinit var mSharedPreferences: SharedPreferences
        private const val pref_file = "settings"

        fun setLocalLanguage(language: String, mContext: Context): Boolean {
                mSharedPreferences = mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = mSharedPreferences.edit()
                editor.putString("lang", language)
                return editor.commit()
        }

        fun getLocalLanguage(mContext: Context): String? {
                mSharedPreferences = mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
                return mSharedPreferences.getString("lang", "ar")
        }

}