package com.example.goldit.notifiedAlerts

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.goldit.login.EMAIL
import com.example.goldit.login.LOGIN_DETAILS
import com.example.goldit.repository.DataSource
import com.example.goldit.repository.Repository
import com.example.goldit.repository.Result
import com.example.goldit.room.Alert
import com.example.goldit.room.LiveRate

const val NOTIFIED_ALERT_PAGE = 1
const val DETAIL_PAGE = 2

class NotifiedAlertViewModel(val repository: DataSource, context: Context) : ViewModel() {

    private val _moveToListPage = MutableLiveData<Boolean>()
    val moveToListPage: LiveData<Boolean>
        get() = _moveToListPage

    private val _moveToDetailPage = MutableLiveData<Boolean>()
    val moveToDetailPage: LiveData<Boolean>
        get() = _moveToDetailPage

    var _page = MutableLiveData<Int>()
    var detailPageId: Long = 0

    private val _alertCountError = MutableLiveData<Boolean>()
    val alertCountError: LiveData<Boolean>
        get() = _alertCountError

    private val sharedPreferences =
        context.getSharedPreferences(LOGIN_DETAILS, Context.MODE_PRIVATE)

    val email = sharedPreferences?.getString(EMAIL, "").toString()

    val notifiedAlertList: LiveData<List<Alert>> = Transformations.map(
        repository.getNotifiedAlerts(
            sharedPreferences.getString(
                EMAIL, ""
            ) ?: ""
        )
    )
    {
        if (it is Result.Success) {
            it.data
        } else {
            listOf()
        }
    }

    fun getLiveRate(type: String): LiveRate? {
        val result = repository.getGoldRateByType(type)
        return if (result is Result.Success) {
            result.data
        } else {
            null
        }

    }

    fun enableAlert(alert: Alert) {

        val result = repository.getUserActiveAlertCount(alert.userEmail)

        if (result is Result.Success) {
            _alertCountError.value = result.data >= 10
        } else {
            _alertCountError.value = true
        }

        if (_alertCountError.value == false) {
            repository.enableAlert(alert.id)
            _moveToListPage.value = true
        } else {
            _moveToListPage.value = false
        }

    }

    fun getAlertDetails(id: Long, userName: String): Alert? {

        val result = repository.getNotifiedAlertById(id, userName)
        return if (result is Result.Success) {
            result.data
        } else {
            null
        }

    }

    fun movedToListPage() {
        _moveToListPage.value = false
    }


}