package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.repositry.WeatherRepositiry
import com.example.myapplication.model.Model

class FavoriteViewModel : ViewModel() {

    private var forecastRepository: WeatherRepositiry
    var favoriteData: LiveData<List<Model>>

    init {
        forecastRepository = WeatherRepositiry()
        favoriteData = MutableLiveData()
    }

}