package com.example.goldit.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CaratDataClass(
    val success:Boolean,
    val base: String,
    val timestamp:Long,
    val data: Data
) :Parcelable{
}

@Parcelize
data class Data(
    @Json(name = "24k") val carat24 :Double,
    @Json(name = "23k") val carat23 :Double,
    @Json(name = "22k") val carat22 :Double,
    @Json(name = "21k") val carat21 :Double,
    @Json(name = "18k") val carat18 :Double,
    @Json(name = "16k") val carat16 :Double,
    @Json(name = "14k") val carat14 :Double,
    @Json(name = "12k") val carat12 :Double,
    @Json(name = "10k") val carat10 :Double,
    @Json(name = "9k") val carat9 :Double,
    @Json(name = "8k") val carat8 :Double,
    @Json(name = "6k") val carat6 :Double
) :Parcelable{
}