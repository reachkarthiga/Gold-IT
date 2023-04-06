package com.example.goldit.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.goldit.login.EMAIL
import com.example.goldit.login.LOGIN_DETAILS
import com.example.goldit.login.USER_NAME
import com.example.goldit.repository.DataSource
import com.example.goldit.repository.Repository
import com.example.goldit.repository.Result
import com.example.goldit.room.*
import kotlinx.coroutines.launch


class HomeViewModel(
    val repository: DataSource, application: Application
) : ViewModel() {

    private val _addNewAlert = MutableLiveData<Boolean>()
    val addNewAlert: LiveData<Boolean>
        get() = _addNewAlert

    private val sharedPreferences =
        application.getSharedPreferences(LOGIN_DETAILS, Context.MODE_PRIVATE)

    var userName: String = sharedPreferences?.getString(USER_NAME, "") ?: ""
    var email: String = sharedPreferences?.getString(EMAIL, "") ?: ""
    var loadingInProgress = false

    val liveRateList: LiveData<List<LiveRate>> = Transformations.map(repository.getLiveRate()) {
        if (it is Result.Success) {
            it.data
        } else {
            listOf()
        }
    }

    val alertList: LiveData<List<Alert>> = Transformations.map(repository.getActiveAlerts(email)) {
        if (it is Result.Success) {
            it.data
        } else {
            listOf()
        }
    }

    private val _userNameClicked = MutableLiveData<Boolean>()
    val userNameClicked: LiveData<Boolean>
        get() = _userNameClicked

    fun addNewClicked() {
        _addNewAlert.value = true
    }

    fun navigatedToSetAlert() {
        _addNewAlert.value = false
    }

    fun userNameClicked() {
        _userNameClicked.value = true
    }

    fun navigatedToProfileDetails() {
        _userNameClicked.value = false
    }

    fun updateLiveRates() {
        if (!loadingInProgress) {
            loadingInProgress = true
            viewModelScope.launch {
                repository.uploadNetworkDataToLocalDatabase()
            }
        }
    }

}
