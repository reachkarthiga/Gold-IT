package com.example.goldit.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.goldit.repository.carat22
import com.example.goldit.repository.carat24
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "live_rate")
data class LiveRate(

    val goldType: String ,
    val goldRate: Int,
    @PrimaryKey
    val id: Long = System.currentTimeMillis()

) : Parcelable {
}