package com.example.goldit.room

import android.os.SystemClock
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

const val PRICE_DROP = "Price Drop"

@Entity(tableName = "alert_details")
data class Alert(
    var goldType:String,
    var goldRate:Int,
    var alertAchieved: Boolean,
    var userEmail: String,
    var alertType:String = PRICE_DROP,
    val achievedDateTime :Long,
    @PrimaryKey
    val id :Long = System.currentTimeMillis()


    )

