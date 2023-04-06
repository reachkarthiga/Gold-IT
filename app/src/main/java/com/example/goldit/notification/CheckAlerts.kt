package com.example.goldit.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.example.goldit.R
import com.example.goldit.login.EMAIL
import com.example.goldit.login.LOGIN_DETAILS
import com.example.goldit.login.USER_NAME
import com.example.goldit.repository.Repository
import com.example.goldit.repository.Result
import com.example.goldit.repository.carat22
import com.example.goldit.repository.carat24
import com.example.goldit.room.Alert
import com.example.goldit.room.DataBase
import kotlinx.coroutines.*
import java.security.Permission
import java.security.Permissions

class CheckAlerts(val repository: Repository, val context: Context) {

    val scope = CoroutineScope(Job() + Dispatchers.Default)

    fun checkAlertsAreAchieved() {

        var price = 0

        val sharedPreferences = context.getSharedPreferences(LOGIN_DETAILS, Context.MODE_PRIVATE)
        val email = sharedPreferences?.getString(EMAIL, "") ?: ""

        // Get 22k gold Rate and check if alerts are achieved

        var result = repository.getGoldRateByType(carat22)
        price = if (result is Result.Success) {
            result.data.goldRate
        } else {
            Int.MAX_VALUE
        }

        val list22K = repository.checkIfAlertAchieved(price, carat22, email)

        // Get 24k gold Rate and check if alerts are achieved

        result = repository.getGoldRateByType(carat24)
        price = if (result is Result.Success) {
            result.data.goldRate
        } else {
            Int.MAX_VALUE
        }

        val list24K = repository.checkIfAlertAchieved(price, carat24, email)

        if (list22K is Result.Success && list24K is Result.Success) {

            if (list22K.data.isNotEmpty() || list24K.data.isNotEmpty()) {

                val id: Long = when {
                    (list22K.data.size == 1 && list24K.data.isEmpty()) -> {
                        list22K.data[0].id
                    }
                    (list24K.data.size == 1 && list22K.data.isEmpty()) -> {
                        list24K.data[0].id
                    }
                    else -> {
                        0
                    }
                }

                sendNotification(context, id)
                updateAlerts(list22K.data, list24K.data)

            }
        }

    }

    private fun updateAlerts(list22K: List<Alert>, list24K: List<Alert>) {

        list22K.forEach {
            scope.launch {
                repository.updateAlert(
                    Alert(
                        it.goldType,
                        it.goldRate,
                        true,
                        it.userEmail,
                        it.alertType,
                        System.currentTimeMillis(),
                        it.id
                    )
                )
            }

        }

        list24K.forEach {
            scope.launch {
                repository.updateAlert(
                    Alert(
                        it.goldType,
                        it.goldRate,
                        true,
                        it.userEmail,
                        it.alertType,
                        System.currentTimeMillis(),
                        it.id
                    )
                )
            }

        }


    }
}


