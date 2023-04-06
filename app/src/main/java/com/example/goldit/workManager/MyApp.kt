package com.example.goldit.workManager

import android.app.Application
import androidx.navigation.fragment.NavHostFragment
import androidx.work.*
import com.example.goldit.MainActivity
import com.example.goldit.MainPage.MainPageViewModel
import com.example.goldit.R
import com.example.goldit.home.HomeViewModel
import com.example.goldit.notifiedAlerts.NotifiedAlertViewModel
import com.example.goldit.repository.DataSource
import com.example.goldit.repository.Repository
import com.example.goldit.room.DataBase
import com.example.goldit.setAlert.SetAlertViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

const val WORK_NAME = "Live_Rate"

class MyApp : Application() {

    val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        callWorkManager()

        val module = module {
            single { DataBase.getInstance(this@MyApp) }
            single<DataSource> { Repository(get()) }
            single { SetAlertViewModel(get(), get() as DataSource) }
            single { HomeViewModel(get(), get()) }
            single { NotifiedAlertViewModel(get(), get()) }
            single { MainPageViewModel() }

        }

        startKoin {
            androidContext(this@MyApp)
            modules(module)
        }

    }


    private fun callWorkManager() {

        scope.launch {

            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<LiveRateWorkManager>(
                2,
            TimeUnit.HOURS).setConstraints(constraints)
                .build()

            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)

        }

    }


}