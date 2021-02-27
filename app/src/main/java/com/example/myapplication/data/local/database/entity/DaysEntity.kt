package com.example.myapplication.data.local.database.entity

import android.os.Parcel
import android.os.Parcelable

data class DaysEntity(
                        val date: Int,
                        val minTemp: Double,
                        val maxTemp: Double,
                        val icon: String,
                        val sunrise:Int,
                        val descrption: String
                        ): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(date)
        parcel.writeDouble(minTemp)
        parcel.writeDouble(maxTemp)
        parcel.writeString(icon)
        parcel.writeInt(sunrise)
        parcel.writeString(descrption)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DaysEntity> {
        override fun createFromParcel(parcel: Parcel): DaysEntity {
            return DaysEntity(parcel)
        }

        override fun newArray(size: Int): Array<DaysEntity?> {
            return arrayOfNulls(size)
        }
    }
}

