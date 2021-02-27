package com.example.myapplication.data.local.database
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.data.local.database.entity.WeatherEntity


@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentWeather(weatherDatabase: WeatherEntity)

    @Query("select * from weather_table where id = 0")
    fun getCurrentWeather(): LiveData<WeatherEntity>

    @Delete
    fun deletetWeather(weatherDatabase: WeatherEntity)

}
