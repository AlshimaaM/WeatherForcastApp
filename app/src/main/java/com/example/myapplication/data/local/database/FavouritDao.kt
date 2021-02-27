package com.example.myapplication.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.data.local.database.entity.FavouritEntity

@Dao
interface FavouritDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavWeather(favoriteModel: FavouritEntity)

    @Query("select * from favorite_table")
    fun getFavWeather(): LiveData<List<FavouritEntity>>


    @Delete
    fun deleteFavWeather(favoriteModel: FavouritEntity)

}
