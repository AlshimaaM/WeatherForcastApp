package com.example.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.local.database.entity.AlertEntity
import com.example.myapplication.data.repositry.WeatherRepositiry

class AlertsViewModel :   ViewModel() {
    private var repo: WeatherRepositiry
    init {
        repo = WeatherRepositiry()
    }

    suspend fun addAlert(alertDatabase: AlertEntity, context: Context) {
        return repo.addAlert(alertDatabase,context)
    }

    fun getAlert(context: Context): LiveData<MutableList<AlertEntity>> {
        return repo.getAlert(context)
    }

    fun deleteAlert(alertDatabase: AlertEntity, context: Context) {
        return repo.deleteAlert(alertDatabase,context)
    }
}