package com.example.myapplication.util

import com.example.myapplication.provider.Setting
import java.text.SimpleDateFormat
import java.util.*

object Constant {
    //d1047ca6aa05bdff768160b146f07e50
    //6998d940b9d8ba78db3acb3425a7cc91
    //517a14f849e519bb4fa84cdbd4755f56
    val API_KEY = "d1047ca6aa05bdff768160b146f07e50"
    val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    fun convertLongToDay(dayConvert: Int):String{
        val dateFormat= SimpleDateFormat("EEE")
        val date= Date()
        date.time=dayConvert.toLong()*1000
        return dateFormat.format(date)

    }
    fun convertArabic(arabicStr: String): String? {
        val chArr = arabicStr.toCharArray()
        val sb = StringBuilder()
        for (ch in chArr) {
            if (Character.isDigit(ch)) {
                sb.append(Character.getNumericValue(ch))
            }else if (ch == '٫'){
                sb.append(".")
            }

            else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

}