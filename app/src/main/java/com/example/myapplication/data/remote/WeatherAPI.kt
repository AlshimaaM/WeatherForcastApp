package com.example.myapplication.data.remote




import com.app.weatherapp.mvvm.data.remote.PlaceReponseOneApi.GeoModel
import com.example.myapplication.model.Model
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("onecall")
    fun getWeather(@Query("lat")lat:String, @Query("lon")lng:String,
                   @Query("exclude")exclude:String, @Query("units")units:String="metric",
                   @Query("lang")languageCode:String, @Query("appid")appid:String): Call<Model>

    @GET("direct")
    fun getPlaceData(
            @Query("q")citName:String="القاهرة",
            @Query("appid")key:String
    ): Call<List<GeoModel>>
}