package com.app.weatherapp.mvvm.data.remote.PlaceReponseOneApi

import com.google.gson.annotations.SerializedName

data class GeoModel (

        @SerializedName("name") val name : String,
        @SerializedName("local_names") val local_names : Local_names,
        @SerializedName("lat") val lat : Double,
        @SerializedName("lon") val lon : Double,
        @SerializedName("country") val country : String
)

data class Local_names (

        @SerializedName("af") val af : String,
        @SerializedName("ar") val ar : String,
        @SerializedName("ascii") val ascii : String,
        @SerializedName("bg") val bg : String,
        @SerializedName("ca") val ca : String,
        @SerializedName("da") val da : String,
        @SerializedName("de") val de : String,
        @SerializedName("el") val el : String,
        @SerializedName("en") val en : String,
        @SerializedName("eu") val eu : String,
        @SerializedName("fa") val fa : String,
        @SerializedName("feature_name") val feature_name : String,
        @SerializedName("fi") val fi : String,
        @SerializedName("fr") val fr : String,
        @SerializedName("gl") val gl : String,
        @SerializedName("he") val he : String,
        @SerializedName("hi") val hi : String,
        @SerializedName("hr") val hr : String,
        @SerializedName("hu") val hu : String,
        @SerializedName("id") val id : String,
        @SerializedName("it") val it : String,
        @SerializedName("ja") val ja : String,
        @SerializedName("la") val la : String,
        @SerializedName("lt") val lt : String,
        @SerializedName("mk") val mk : String,
        @SerializedName("nl") val nl : String,
        @SerializedName("no") val no : String,
        @SerializedName("pl") val pl : String,
        @SerializedName("pt") val pt : String,
        @SerializedName("ro") val ro : String,
        @SerializedName("ru") val ru : String,
        @SerializedName("sk") val sk : String,
        @SerializedName("sl") val sl : String,
        @SerializedName("sr") val sr : String,
        @SerializedName("th") val th : String,
        @SerializedName("tr") val tr : String,
        @SerializedName("vi") val vi : String
)