
package com.example.myapplication.data.local.database.entity
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class FavouritEntity(
    @PrimaryKey(autoGenerate = true)
    val dt: Int,
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    val clouds: Int,
    val wind_speed: Double,
    val icon: String,
    val desc: String,
    val city: String,
    val hourlyWeather: List<HoursEntity>,
    val dailyWeather: List<DaysEntity>
): Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readDouble(),
                parcel.readInt(),
                parcel.readInt(),
                parcel.readInt(),
                parcel.readDouble(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.createTypedArrayList(HoursEntity)!!,
                parcel.createTypedArrayList(DaysEntity)!!
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeInt(dt)
                parcel.writeDouble(temp)
                parcel.writeInt(pressure)
                parcel.writeInt(humidity)
                parcel.writeInt(clouds)
                parcel.writeDouble(wind_speed)
                parcel.writeString(icon)
                parcel.writeString(desc)
                parcel.writeString(city)
                parcel.writeTypedList(hourlyWeather)
                parcel.writeTypedList(dailyWeather)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<FavouritEntity> {
                override fun createFromParcel(parcel: Parcel): FavouritEntity {
                        return FavouritEntity(parcel)
                }

                override fun newArray(size: Int): Array<FavouritEntity?> {
                        return arrayOfNulls(size)
                }
        }
}
