/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.goldit.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.goldit.MainActivity
import com.example.goldit.R

private const val NOTIFICATION_ID = 0
private const val NOTIFICATION_CHANNEL_ID = "Gold Price Alerts"
const val ALERT_ID = "Alert_Id"

fun sendNotification(context: Context, id: Long) {

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    val pendingIntent: PendingIntent = if (id > 0) {

        val bundle = Bundle()
        bundle.putLong(ALERT_ID, id)

        NavDeepLinkBuilder(context.applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation)
            .setDestination(R.id.notifiedAlertDetailFragment)
            .setArguments(bundle)
            .createPendingIntent()

    } else {

        val bundle = Bundle()
        bundle.putLong(ALERT_ID, Long.MIN_VALUE)

        NavDeepLinkBuilder(context.applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation)
            .setArguments(bundle)
            .addDestination(R.id.mainPage, null)
            .createPendingIntent()

    }


    pendingIntent.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }

    val builder = NotificationCompat.Builder(
        context,
        NOTIFICATION_CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_gold_type)
        .setContentTitle(context.resources.getString(R.string.app_name))
        .setContentText("Gold Prices have dropped down. Check the rates")
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)

    val notificationChannel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        context.resources.getString(R.string.channelId), NotificationManager.IMPORTANCE_HIGH
    )

    notificationChannel.enableVibration(true)
    notificationChannel.enableLights(true)
    notificationChannel.setShowBadge(true)

    notificationManager.createNotificationChannel(notificationChannel)

    notificationManager.notify(NOTIFICATION_ID, builder.build())

}

