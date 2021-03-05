package com.example.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.FavouritEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.repositry.WeatherRepositiry
import com.example.myapplication.model.Model
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {

    private var forecastRepository: WeatherRepositiry
    var favoriteData: LiveData<List<Model>>

    init {
        forecastRepository = WeatherRepositiry()
        favoriteData = MutableLiveData()
    }
    suspend fun addFavoriteIntoDB(favouritDatabase: FavouritEntity, context: Context) {
        viewModelScope.launch {
            forecastRepository.favoriteIntoDatabase(favouritDatabase, context)
        }
    }

    fun getFavoriteFromDB(context: Context): LiveData<List<FavouritEntity>> {
        return forecastRepository.favoriteFromDatabase(context)
    }
    fun deleteFav(favouritDatabase: FavouritEntity, context: Context) {
       forecastRepository.deletFavorite(favouritDatabase,context)
    }
    fun dataInDatabase(model: Model): FavouritEntity {
        val hourlyWeather = arrayListOf<HoursEntity>()
        for (hourlyItem in model.hourly) {
            hourlyWeather.add(
                    HoursEntity(
                            hourlyItem.dt.toInt(),
                            hourlyItem.temp,
                            hourlyItem.weather[0].icon
                    )
            )
        }
        val dailyWeather = arrayListOf<DaysEntity>()
        for (dailyItem in model.daily) {
            dailyWeather.add(
                    DaysEntity(
                            dailyItem.dt,
                            dailyItem.temp.min,
                            dailyItem.temp.max,
                            dailyItem.weather[0].icon,
                            dailyItem.sunrise,
                            dailyItem.weather[0].description
                    )
            )
        }
        val database1 = FavouritEntity(
                model.current.dt,
                model.current.temp,
                model.current.pressure,
                model.current.humidity,
                model.current.clouds,
                model.current.wind_speed,
                model.current.weather[0].icon,
                model.current.weather[0].description,
                model.timezone,
                hourlyWeather,
                dailyWeather
        )
        return database1
    }

}