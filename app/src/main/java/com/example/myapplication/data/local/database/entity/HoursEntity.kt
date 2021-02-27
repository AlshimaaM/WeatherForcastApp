package com.example.myapplication.data.local.database.entity

import android.os.Parcel
import android.os.Parcelable

data class HoursEntity(val date: Int,
                       val tempture: Double,
                       val icon: String): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(date)
        parcel.writeDouble(tempture)
        parcel.writeString(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HoursEntity> {
        override fun createFromParcel(parcel: Parcel): HoursEntity {
            return HoursEntity(parcel)
        }

        override fun newArray(size: Int): Array<HoursEntity?> {
            return arrayOfNulls(size)
        }
    }
}
