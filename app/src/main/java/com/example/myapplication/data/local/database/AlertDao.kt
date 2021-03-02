package com.example.myapplication.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.data.local.database.entity.AlertEntity

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun insertAlert(alertDatabase: AlertEntity)

    @Query("select * from alert_table")
    fun getAlerts(): LiveData<MutableList<AlertEntity>>


    @Delete
    suspend fun deleteAlert(alertDatabase: AlertEntity)

}