package com.example.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.local.database.entity.FavouritEntity
import com.example.myapplication.data.repositry.WeatherRepositiry
import com.example.myapplication.model.Model

class FavoriteViewModel : ViewModel() {

    private var forecastRepository: WeatherRepositiry
    var favoriteData: LiveData<List<Model>>

    init {
        forecastRepository = WeatherRepositiry()
        favoriteData = MutableLiveData()
    }
    suspend fun addFavoriteIntoDB(favouritDatabase: FavouritEntity, context: Context) {
        forecastRepository.favoriteIntoDatabase(favouritDatabase,context)
    }
    fun getFavoriteFromDB(context: Context): LiveData<List<FavouritEntity>> {
        return forecastRepository.favoriteFromDatabase(context)
    }
    fun deleteFav(favouritDatabase: FavouritEntity, context: Context) {
       forecastRepository.deletFavorite(favouritDatabase,context)
    }

}