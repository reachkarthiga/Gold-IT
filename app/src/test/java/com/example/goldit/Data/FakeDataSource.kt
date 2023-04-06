package com.example.goldit.Data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.goldit.repository.DataSource
import com.example.goldit.repository.Result
import com.example.goldit.repository.carat24
import com.example.goldit.room.Alert
import com.example.goldit.room.LiveRate

class FakeDataSource(
    val alertList: MutableList<Alert> = mutableListOf(),
    val ratesList: MutableList<LiveRate> = mutableListOf()
) : DataSource {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun uploadNetworkDataToLocalDatabase() {
    }

    override suspend fun updateAlert(alert: Alert) {
        alertList.forEach {
            if (it.id == alert.id) {
                alertList.remove(it)
                alertList.add(alert)
            }
        }
    }

    override suspend fun addAlert(alert: Alert) {
        alertList.add(alert)
    }

    override suspend fun deleteAlert(alertId: Long) {
        alertList.forEach {
            if (it.id == alertId) {
                alertList.remove(it)
            }
        }
    }

    private val alerts = MutableLiveData<Result<List<Alert>>>()

    override fun getActiveAlerts(user: String): LiveData<Result<List<Alert>>> {
        alerts.value = Result.Success(alertList)
        return alerts.map {
            it
        }
    }

    override fun getActiveAlertById(alertId: Long, user: String): Result<Alert> {

        if (shouldReturnError) {
           return Result.Error("Error")
        }

        alertList.forEach {
            if (it.id == alertId && it.userEmail == user && !it.alertAchieved) {
                return Result.Success(it)
            }
        }
        return Result.Error("No Data Found")
    }

    override fun getNotifiedAlerts(user: String): LiveData<Result<List<Alert>>> {

        alerts.value = Result.Success(alertList)
        return alerts.map {
            it
        }
    }

    override fun getNotifiedAlertById(alertId: Long, user: String): Result<Alert> {

        alertList.forEach {
            if (it.id == alertId && it.userEmail == user && it.alertAchieved) {
                return Result.Success(it)
            }
        }
        return Result.Error("No Data Found")
    }

    override fun checkIfAlertAchieved(
        price: Int,
        goldType: String,
        user: String
    ): Result<List<Alert>> {

        return try {
            val returnList = mutableListOf<Alert>()
            alertList.forEach {
                if (it.userEmail == user && it.goldType == goldType && it.goldRate >= price) {
                    returnList.add(it)
                }
            }

            Result.Success(returnList)
        } catch (e:Exception) {
            Result.Error(e.message)
        }

    }

    override fun enableAlert(id: Long) {
        alertList.forEach {
            if (it.id == id) {
                alertList.add(
                    Alert(
                        it.goldType,
                        it.goldRate,
                        false,
                        it.userEmail,
                        it.alertType,
                        0,
                        it.id
                    )
                )
                alertList.remove(it)
            }
        }
    }

    override fun getUserActiveAlertCount(user: String): Result<Int> {

        val result = alertList.filter {
            (!it.alertAchieved && it.userEmail == user)
        }

        return Result.Success(result.size)
    }

    override fun deletePreviousRates() {
        ratesList.clear()
    }

    override fun getGoldRateByType(type: String): Result<LiveRate> {
        ratesList.forEach {
            if (it.goldType == type) {
                return Result.Success(it)
            }
        }

        return Result.Error("No rates found")
    }

    private val rates = MutableLiveData<Result<List<LiveRate>>>()

    override fun getLiveRate(): LiveData<Result<List<LiveRate>>> {
        rates.value = Result.Success(ratesList)
        return rates
    }

    override suspend fun insertLiveRate(liveRate: LiveRate) {
        ratesList.add(liveRate)
    }

}