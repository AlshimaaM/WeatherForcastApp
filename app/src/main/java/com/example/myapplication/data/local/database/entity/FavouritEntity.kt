
package com.example.myapplication.data.local.database.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class FavouritEntity(
    @PrimaryKey(autoGenerate = true)
        var id: Int,
    val dt: Int,
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    val clouds: Int,
    val wind_speed: Double,
    val icon: String,
    val desc: String,
    val city: String,
    val hourlyWeather: List<HoursEntity>,
    val dailyWeather: List<DaysEntity>
)
