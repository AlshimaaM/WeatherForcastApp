package com.example.myapplication.data.local.database

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.local.database.entity.AlertEntity

import com.example.myapplication.data.local.database.entity.FavouritEntity
import com.example.myapplication.data.local.database.entity.WeatherEntity

@Database(
    entities = arrayOf(WeatherEntity::class, FavouritEntity::class, AlertEntity::class),
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WeatherDatabaseInstance : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favouritDao(): FavouritDao
    abstract fun alertDao(): AlertDao
    companion object {

       @Volatile
       private var INSTANCE: WeatherDatabaseInstance? = null

       fun getInstance(context: Context): WeatherDatabaseInstance {
           synchronized(this) {
               var instance = INSTANCE

               if (instance == null) {
                   instance = Room.databaseBuilder(
                       context.applicationContext,
                       WeatherDatabaseInstance::class.java,
                       "weather_database"
                   )
                       .fallbackToDestructiveMigration()
                       .build()
                   INSTANCE = instance
               }
               return instance
           }
       }
   }

}
