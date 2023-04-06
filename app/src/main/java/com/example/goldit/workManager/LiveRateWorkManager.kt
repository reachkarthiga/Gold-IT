package com.example.goldit.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.goldit.notification.CheckAlerts
import com.example.goldit.repository.Repository
import com.example.goldit.room.DataBase

class LiveRateWorkManager(private val context: Context, params: WorkerParameters) :CoroutineWorker(context, params)  {

    override suspend fun doWork(): Result {

        val repository = Repository(DataBase.getInstance(context))

        return try {

            repository.uploadNetworkDataToLocalDatabase()

            val checkAlerts = CheckAlerts(repository, context)
            checkAlerts.checkAlertsAreAchieved()

            Result.success()
        } catch (e:Exception) {
            Result.retry()
        }



    }

}