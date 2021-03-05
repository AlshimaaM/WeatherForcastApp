package com.example.myapplication.data.repositry

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.weatherapp.mvvm.data.remote.PlaceReponseOneApi.GeoModel
import com.example.myapplication.data.local.database.WeatherDatabaseInstance
import com.example.myapplication.data.local.database.entity.AlertEntity
import com.example.myapplication.data.local.database.entity.FavouritEntity
import com.example.myapplication.data.local.database.entity.WeatherEntity
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.model.Model
import com.example.myapplication.provider.Setting
import com.example.myapplication.util.ContextUtils.Companion.offlineToast
import com.example.myapplication.view.activity.MainActivity
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class WeatherRepositiry {
    private  var weatherMutableLiveData: MutableLiveData<Model> = MutableLiveData()
    private  var locationMutableLiveData: MutableLiveData<GeoModel> = MutableLiveData()

    fun getWeather(latitude: String, longitude: String): MutableLiveData<Model> {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.getCurrentLocationweather(
                    latitude,
                    longitude,
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

    fun getPlace(location: String): MutableLiveData<GeoModel> {
        CoroutineScope(Dispatchers.IO).launch {
            try {
            val response = RetrofitInstance.getSearchLocation(location = location).execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    locationMutableLiveData.value = response.body()!!.get(0)
                }
            }
        } catch (e: Exception) {
        }}

        return locationMutableLiveData
    }

    suspend  fun weatherDatabase(wDB: WeatherEntity, context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context)
          database.weatherDao().insertCurrentWeather(wDB)

    }
    fun getWeatherDatabase(context: Context): LiveData<WeatherEntity> {
        val database = WeatherDatabaseInstance.getInstance(context)
        return database.weatherDao().getCurrentWeather()
    }

    suspend  fun favoriteIntoDatabase(fDatabase: FavouritEntity, context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context)
            database.favouritDao().insertFavWeather(fDatabase)
    }

    fun favoriteFromDatabase(context: Context): LiveData<List<FavouritEntity>> {
        val database = WeatherDatabaseInstance.getInstance(context)
        return database.favouritDao().getFavWeather()
    }
    fun deletFavorite(fDatabase: FavouritEntity, context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context).favouritDao().deleteFavWeather(fDatabase)

    }

    suspend fun addAlert(alertDatabase: AlertEntity, context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context)
        GlobalScope.launch {
            Dispatchers.IO
            database.alertDao().insertAlert(alertDatabase)
        }
    }

    fun getAlert(context: Context): LiveData<MutableList<AlertEntity>> {
        val database = WeatherDatabaseInstance.getInstance(context)
        return database.alertDao().getAlerts()
    }

    fun deleteAlert(alertDatabase: AlertEntity,context: Context) {
        val database = WeatherDatabaseInstance.getInstance(context)
        GlobalScope.launch {
            Dispatchers.IO
            database.alertDao().deleteAlert(alertDatabase)
        }
    }

}