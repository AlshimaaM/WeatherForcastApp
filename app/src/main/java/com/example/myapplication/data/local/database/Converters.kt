package com.example.myapplication.data.local.database

import androidx.room.TypeConverter
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.model.AlertsItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun fromListHourlyToString(listHourly: List<HoursEntity>): String {
        return Gson().toJson(listHourly)
    }

    @TypeConverter
    fun fromStringToListHourly(stringHourly: String): List<HoursEntity> {
        val listType: Type = object : TypeToken<List<HoursEntity>>() {}.type
        return Gson().fromJson(stringHourly, listType)
    }

    @TypeConverter
    fun fromListDailyToString(listDaily: List<DaysEntity>): String {
        return Gson().toJson(listDaily)
    }

    @TypeConverter
    fun fromStringToListDaily(stringDaily: String): List<DaysEntity> {
        val listType: Type = object : TypeToken<List<DaysEntity>>() {}.type
        return Gson().fromJson(stringDaily, listType)
    }
    @TypeConverter
    fun fromListAlertToString(listAlert: List<AlertsItem>): String {
        return Gson().toJson(listAlert)
    }

    @TypeConverter
    fun fromStringToListAlert(stringAlert: String): List<AlertsItem> {
        val listType: Type = object : TypeToken<List<AlertsItem>>() {}.type
        return Gson().fromJson(stringAlert, listType)
    }
}