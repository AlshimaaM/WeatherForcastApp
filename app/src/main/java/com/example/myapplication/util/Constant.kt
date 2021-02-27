package com.example.myapplication.util

import java.text.SimpleDateFormat
import java.util.*

object Constant {
    val API_KEY = "6998d940b9d8ba78db3acb3425a7cc91"
    val BASE_URL = "https://api.openweathermap.org/data/2.5/"
   val GEO_URL= "http://api.openweathermap.org/geo/1.0/"

    fun convertLongToDay(dayConvert: Int):String{
        val dateFormat= SimpleDateFormat("EEE")
        val date= Date()
        date.time=dayConvert.toLong()*1000
        return dateFormat.format(date)

    }
}