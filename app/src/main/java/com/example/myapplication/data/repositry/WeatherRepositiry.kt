package com.example.myapplication.data.repositry

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.local.database.WeatherDatabaseInstance
import com.example.myapplication.data.local.database.entity.AlertEntity
import com.example.myapplication.data.local.database.entity.FavouritEntity
import com.example.myapplication.data.local.database.entity.WeatherEntity
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.model.Model
import com.example.myapplication.provider.Setting
import com.example.myapplication.util.Constant.convertArabic
import kotlinx.coroutines.*
import java.lang.Exception

class WeatherRepositiry(application: Application) {
    private  var weatherMutableLiveData: MutableLiveData<Model> = MutableLiveData()
  //  private val shared :SharedPrefrence= SharedPrefrence(application.applicationContext)
    fun getWeather(latitude: String, longitude: String): MutableLiveData<Model> {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var latt :String?= latitude
                var lonn :String?=longitude
          if (Setting.languageSystem.equals("ar")){
              latt =convertArabic(latitude)
              lonn =convertArabic(longitude)
          }
                val response = RetrofitInstance.getCurrentLocationweather(
                    latt!!,
                    lonn!!,
                   Setting.languageSystem,
                   Setting.unitSystem
                ).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        weatherMutableLiveData.value = response.body()
                    }
                }
            } catch (e: Exception) {
             //  Toast.makeText(coroutineContext,e.localizedMessage,Toast.LENGTH_LONG).show()
                println("llllllllllllllllllllllllllllllllll"+e.message)
            }
            }

        return weatherMutableLiveData
    }

    suspend  fun weatherDatabase(wDB: WeatherEntity, context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            database.weatherDao().insertCurrentWeather(wDB)
        }
    }

    fun getWeatherDatabase(context: Context): LiveData<WeatherEntity> {
        val database = WeatherDatabaseInstance.getInstance(context)
        return database.weatherDao().getCurrentWeather()
    }

    suspend  fun favoriteIntoDatabase(fDatabase: FavouritEntity, context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            database.favouritDao().insertFavWeather(fDatabase)
        }
    }

    fun favoriteFromDatabase(context: Context): LiveData<List<FavouritEntity>> {
        val database = WeatherDatabaseInstance.getInstance(context)
        return database.favouritDao().getFavWeather()
    }

    fun deletFavorite(fDatabase: FavouritEntity, context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            database.favouritDao().deleteFavWeather(fDatabase)
        }
    }

    suspend fun addAlert(alertDatabase: AlertEntity, context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            database.alertDao().insertAlert(alertDatabase)
        }
    }

    fun getAlert(context: Context): LiveData<MutableList<AlertEntity>> {
        val database = WeatherDatabaseInstance.getInstance(context)
        return database.alertDao().getAlerts()
    }
    fun getAlertAlerm(context: Context): WeatherEntity {
        val database = WeatherDatabaseInstance.getInstance(context)
        return database.weatherDao().getCurrentWeatherAlert()
        //coroutine
    }
    fun deleteAlert(alertDatabase: AlertEntity,context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch  {
            database.alertDao().deleteAlert(alertDatabase)
        }
    }

}