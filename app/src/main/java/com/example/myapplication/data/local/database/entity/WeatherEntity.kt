package com.example.myapplication.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val date: Int,
    val tempture: Double,
    val pressure: Int,
    val humidity: Int,
    val clouds: Int,
    val wind_speed: Double,
    val icon: String,
    val descrption: String,
    val city: String,
    val hour_Weather: List<HoursEntity>,
    val dail_Weather: List<DaysEntity>
)