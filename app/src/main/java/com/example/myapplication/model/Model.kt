package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class Model(
        @SerializedName("lat") val lat : Double,
        @SerializedName("lon") val lon : Double,
        @SerializedName("timezone") val timezone : String,
        @SerializedName("timezone_offset") val timezone_offset : Int,
        @SerializedName("current") val current : Current,
        @SerializedName("hourly") val hourly : List<Hourly>,
        @SerializedName("daily") val daily : List<Daily>,
        @SerializedName("alerts") val alerts: List<AlertsItem>)


data class AlertsItem(
        @SerializedName("sender_name") val senderName: String,
        @SerializedName("start") val start: Int,
        @SerializedName("description") val description: String,
        @SerializedName("end") val end: Int,
        @SerializedName("event") val event: String
)
data class Current (
        @SerializedName("dt") val dt : Int,
        @SerializedName("sunrise") val sunrise : Int,
        @SerializedName("sunset") val sunset : Int,
        @SerializedName("temp") val temp : Double,
        @SerializedName("feels_like") val feels_like : Double,
        @SerializedName("pressure") val pressure : Int,
        @SerializedName("humidity") val humidity : Int,
        @SerializedName("dew_point") val dew_point : Double,
        @SerializedName("uvi") val uvi : Double,
        @SerializedName("clouds") val clouds : Int,
        @SerializedName("visibility") val visibility : Int,
        @SerializedName("wind_speed") val wind_speed : Double,
        @SerializedName("wind_deg") val wind_deg : Int,
        @SerializedName("weather") val weather : List<Weather>
)
data class Weather (
        @SerializedName("id") val id : Int,
        @SerializedName("main") val main : String,
        @SerializedName("description") val description : String,
        @SerializedName("icon") val icon : String
)
data class Hourly (
        @SerializedName("dt") val dt : Double,
        @SerializedName("temp") val temp : Double,
        @SerializedName("feels_like") val feels_like : Double,
        @SerializedName("pressure") val pressure : Int,
        @SerializedName("humidity") val humidity : Int,
        @SerializedName("dew_point") val dew_point : Double,
        @SerializedName("uvi") val uvi : Double,
        @SerializedName("clouds") val clouds : Int,
        @SerializedName("visibility") val visibility : Int,
        @SerializedName("wind_speed") val wind_speed : Double,
        @SerializedName("wind_deg") val wind_deg : Int,
        @SerializedName("weather") val weather : List<Weather>,
        @SerializedName("pop") val pop : Double
)

data class Daily (
        @SerializedName("dt") val dt : Int,
        @SerializedName("sunrise") val sunrise : Int,
        @SerializedName("sunset") val sunset : Int,
        @SerializedName("temp") val temp : Temp,
        @SerializedName("feels_like") val feels_like : Feels_like,
        @SerializedName("pressure") val pressure : Int,
        @SerializedName("humidity") val humidity : Int,
        @SerializedName("dew_point") val dew_point : Double,
        @SerializedName("wind_speed") val wind_speed : Double,
        @SerializedName("wind_deg") val wind_deg : Int,
        @SerializedName("weather") val weather : List<Weather>,
        @SerializedName("clouds") val clouds : Int,
        @SerializedName("pop") val pop : Double,
        @SerializedName("uvi") val uvi : Double
)
data class Feels_like (
        @SerializedName("day") val day : Double,
        @SerializedName("night") val night : Double,
        @SerializedName("eve") val eve : Double,
        @SerializedName("morn") val morn : Double
)

data class Temp (
        @SerializedName("day") val day : Double,
        @SerializedName("min") val min : Double,
        @SerializedName("max") val max : Double,
        @SerializedName("night") val night : Double,
        @SerializedName("eve") val eve : Double,
        @SerializedName("morn") val morn : Double
)

