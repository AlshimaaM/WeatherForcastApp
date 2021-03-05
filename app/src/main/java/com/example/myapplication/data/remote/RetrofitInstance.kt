package com.example.myapplication.data.remote

import com.example.myapplication.model.Model
import com.example.myapplication.util.Constant
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

object RetrofitInstance {
    private lateinit var retrofit: Retrofit

    fun getInstance(): Retrofit {
        retrofit = Retrofit.Builder().baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        return retrofit
    }

    fun getWeatherAPI(): WeatherAPI {
        return getInstance().create(WeatherAPI::class.java)
    }

    fun getCurrentLocationweather(
        latitude: String,
        longitude: String,
        languageCode: String,
        units: String): Call<Model> {
        return getWeatherAPI()
            .getWeather(
                lat = latitude,
                lng = longitude,
                exclude = "minutely",
                languageCode = languageCode,
                units = units,
                appid = Constant.API_KEY
            )
    }

    fun getImage(icon: String): String {
        return "http://openweathermap.org/img/w/${icon}.png"
    }

    val dateNow: String
        get() {
            val dateFormat = SimpleDateFormat("EEE,dd MM yyyy hh:mm a")
            val date = Date()
            return dateFormat.format(date)
        }

    fun formateTime(format: Int): String {
        val dateFormat = SimpleDateFormat("HH:mm a")
        val date = Date()
        date.time = format.toLong() * 1000
        return dateFormat.format(date)

    }
}