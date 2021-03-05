package com.example.myapplication.viewmodel

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.weatherapp.mvvm.data.remote.PlaceReponseOneApi.GeoModel
import com.example.myapplication.HandlingLocation
import com.example.myapplication.data.local.database.entity.WeatherEntity
import com.example.myapplication.data.repositry.WeatherRepositiry
import com.example.myapplication.model.Model

class WeatherViewModel : ViewModel() {

    private var weatherRepositiry: WeatherRepositiry = WeatherRepositiry()
      var weatherMutableLiveData: MutableLiveData<Model> = MutableLiveData()
      var locationMutableLiveData: MutableLiveData<GeoModel> = MutableLiveData()

    fun fetchweather(lat: String, lng: String): MutableLiveData<Model> {
        weatherMutableLiveData = weatherRepositiry.getWeather(lat, lng)
        return weatherMutableLiveData
    }
    suspend fun weatherDatabase(weatherdatabase: WeatherEntity, context: Context) {
        weatherRepositiry.weatherDatabase(weatherdatabase, context)
    }

    fun getWeather(context: Context): LiveData<WeatherEntity> {
        return weatherRepositiry.getWeatherDatabase(context)
    }

    fun getPlace(location:String): MutableLiveData<GeoModel> {
        locationMutableLiveData = weatherRepositiry.getPlace(location)
        return locationMutableLiveData
    }
}