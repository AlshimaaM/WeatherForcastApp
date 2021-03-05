package com.example.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.local.database.entity.WeatherEntity
import com.example.myapplication.data.repositry.WeatherRepositiry
import com.example.myapplication.model.AlertsItem
import com.example.myapplication.model.Model
import com.example.myapplication.provider.Setting
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private var weatherRepositiry: WeatherRepositiry = WeatherRepositiry()
      var weatherMutableLiveData: MutableLiveData<Model> = MutableLiveData()

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