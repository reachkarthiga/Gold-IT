package com.example.goldit.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LiveRateDao {

    @Insert
    suspend fun insertLiveRate(liveRate: LiveRate)

    @Query("Select * from live_rate order by id desc limit 2")
    fun getLiveRate() :LiveData<List<LiveRate>>

    @Query("Select * from live_rate where goldType = :type order by id desc limit 1 ")
    fun getGoldRateByType(type:String) :LiveRate

    @Query("delete from live_rate")
    fun deletePreviousRates()

}