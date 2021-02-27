package com.example.myapplication.data.local.database.entity

data class DaysEntity(
                        val date: Int,
                        val minTemp: Double,
                        val maxTemp: Double,
                        val icon: String,
                        val sunrise:Int,
                        val descrption: String
                        )
