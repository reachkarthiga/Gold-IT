package com.example.goldit.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.goldit.BuildConfig
import com.example.goldit.network.CaratAPI
import com.example.goldit.network.CaratDataClass
import com.example.goldit.room.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

const val carat24 = "24K"
const val carat22 = "22K"

class Repository(
    val dataBase: DataBase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataSource {

    private val currency = "INR"

    override suspend fun uploadNetworkDataToLocalDatabase() {

        withContext(ioDispatcher) {

            try {
             val networkData = CaratAPI.retrofitService.getProperties(BuildConfig.CARAT_API_KEY, currency)
            //      val networkData = CaratAPI.retrofitService.getProperties()

                if (networkData.success) {

                    dataBase.liveRateDao.deletePreviousRates()

                    Log.i("networkData", networkData.toString())

                    dataBase.liveRateDao.insertLiveRate(
                        LiveRate(carat24, networkData.data.carat24.toInt() * 5)
                    )

                    dataBase.liveRateDao.insertLiveRate(
                        LiveRate(carat22, networkData.data.carat22.toInt() * 5)
                    )

                }

            } catch (e:Exception) {
                Log.i("Network Error", e.message.toString())
                return@withContext
            }


        }
    }

    override suspend fun updateAlert(alert: Alert) {
        withContext(ioDispatcher) {
            dataBase.alertDao.updateAlert(alert)
        }
    }

    override suspend fun addAlert(alert: Alert) {
        dataBase.alertDao.saveAlert(alert)
    }

    override suspend fun deleteAlert(alertId: Long) {
        dataBase.alertDao.deleteAlert(alertId)
    }

    override fun getActiveAlerts(user: String): LiveData<Result<List<Alert>>> {
        return dataBase.alertDao.getActiveAlerts(user).map {
            Result.Success(it)
        }
    }

    override fun getActiveAlertById(alertId: Long, user: String): Result<Alert> {
        return try {
            Result.Success(dataBase.alertDao.getActiveAlertById(alertId, user))
        } catch (e: Exception) {
            Result.Error("No Data Found")
        }
    }

    override fun getNotifiedAlerts(user: String): LiveData<Result<List<Alert>>> {
        return dataBase.alertDao.getNotifiedAlerts(user).map {
            Result.Success(it)
        }
    }

    override fun getNotifiedAlertById(alertId: Long, user: String): Result<Alert> {
        return try {
            Result.Success(dataBase.alertDao.getNotifiedAlertById(alertId, user))
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }

    }

    override fun checkIfAlertAchieved(
        price: Int,
        goldType: String,
        user: String
    ): Result<List<Alert>> {

        return try {
            Result.Success(dataBase.alertDao.checkIfAlertAchieved(price, goldType, user))
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }

    }

    override fun enableAlert(id: Long) {
        dataBase.alertDao.enableAlert(id)
    }

    override fun getUserActiveAlertCount(user: String): Result<Int> {
        return try {
            Result.Success(dataBase.alertDao.getUserActiveAlertCount(user))
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    override fun deletePreviousRates() {
        dataBase.liveRateDao.deletePreviousRates()
    }

    override fun getGoldRateByType(type: String): Result<LiveRate> {
        return try {
            Result.Success(dataBase.liveRateDao.getGoldRateByType(type))
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    override fun getLiveRate(): LiveData<Result<List<LiveRate>>> {
        return dataBase.liveRateDao.getLiveRate().map {
            Result.Success(it)
        }
    }

    override suspend fun insertLiveRate(liveRate: LiveRate) {
        dataBase.liveRateDao.insertLiveRate(liveRate)
    }

}