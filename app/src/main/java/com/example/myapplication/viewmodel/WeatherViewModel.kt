package com.example.myapplication.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.local.database.entity.WeatherEntity
import com.example.myapplication.data.repositry.WeatherRepositiry
import com.example.myapplication.model.AlertsItem
import com.example.myapplication.model.Model
import com.example.myapplication.provider.Setting
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application)  : AndroidViewModel(application) {

    private var weatherRepositiry: WeatherRepositiry = WeatherRepositiry(application)
      var weatherMutableLiveData: MutableLiveData<Model> = MutableLiveData()
    private var progress :MutableLiveData<Int> =MutableLiveData<Int>()

    fun internetAvailable(context: Context): Boolean {
        var connected = false
        var connected1 = false
        var connected2 = false
        val s = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(s) as ConnectivityManager?
        val info = manager?.activeNetworkInfo
        if (info != null && info.isConnected) {
            connected = info.type == ConnectivityManager.TYPE_WIFI
            connected1 = info.type == ConnectivityManager.TYPE_MOBILE
            if (connected || connected1) {
                connected2 = true
            }
        } else {
            connected2 = false
        }
        return connected2
    }

    fun fetchweather(lat: String, lng: String): MutableLiveData<Model> {
        weatherMutableLiveData = weatherRepositiry.getWeather(lat, lng)
        return weatherMutableLiveData
    }
    suspend fun weatherDatabase(weatherdatabase: WeatherEntity, context: Context) {
        viewModelScope.launch {
            weatherRepositiry.weatherDatabase(weatherdatabase, context)
        }
    }

    fun getWeather(context: Context): LiveData<WeatherEntity> {
        return weatherRepositiry.getWeatherDatabase(context)
    }
    fun writeIntoDatabase(model: Model): WeatherEntity {
        val hoursListDB = arrayListOf<HoursEntity>()
        for (hourItem in model.hourly) {
            hoursListDB.add(
                    HoursEntity(
                            hourItem.dt.toInt(),
                            hourItem.temp,
                            hourItem.weather[0].icon
                    )
            )
        }
        val daysListDB = arrayListOf<DaysEntity>()
        for (dayItem in model.daily) {
            daysListDB.add(
                    DaysEntity(
                            dayItem.dt,
                            dayItem.temp.min,
                            dayItem.temp.max,
                            dayItem.weather[0].icon,
                            dayItem.sunrise,
                            dayItem.weather[0].description
                    )
            )
        }
        val alertList = arrayListOf<AlertsItem>()
        if (model.alerts != null) {
            for (alertItem in model.alerts) {
                alertList.add(
                        AlertsItem(
                                alertItem.senderName,
                                alertItem.start,
                                alertItem.description,
                                alertItem.end,
                                alertItem.event
                        )
                )
            }
        }
        val weatherDatabase = WeatherEntity(
                0,
                model.current.dt,
                model.current.temp,
                model.current.pressure,
                model.current.humidity,
                model.current.clouds,
                model.current.wind_speed,
                model.current.weather[0].icon,
                model.current.weather[0].description,
                model.timezone,
                hoursListDB,
                daysListDB,
                alertList
        )
        return weatherDatabase
    }
}