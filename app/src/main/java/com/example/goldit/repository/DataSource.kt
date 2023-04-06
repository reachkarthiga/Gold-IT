package com.example.goldit.repository

import androidx.lifecycle.LiveData
import com.example.goldit.room.Alert
import com.example.goldit.room.LiveRate

interface DataSource {

    suspend fun uploadNetworkDataToLocalDatabase()
    suspend fun updateAlert(alert: Alert)
    suspend fun addAlert(alert: Alert)
    suspend fun deleteAlert(alertId: Long)
    fun getActiveAlerts(user:String) :LiveData<Result<List<Alert>>>
    fun getActiveAlertById(alertId: Long, user:String) : Result<Alert>
    fun getNotifiedAlerts(user: String) :LiveData<Result<List<Alert>>>
    fun getNotifiedAlertById(alertId: Long, user: String) :Result<Alert>
    fun checkIfAlertAchieved(price:Int, goldType:String, user: String) :Result<List<Alert>>
    fun enableAlert(id:Long)
    fun getUserActiveAlertCount(user:String) :Result<Int>

    fun deletePreviousRates()
    fun getGoldRateByType(type:String) :Result<LiveRate>
    fun getLiveRate() :LiveData<Result<List<LiveRate>>>
    suspend fun insertLiveRate(liveRate: LiveRate)

}