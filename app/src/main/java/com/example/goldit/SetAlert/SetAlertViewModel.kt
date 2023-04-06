package com.example.goldit.setAlert

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.goldit.login.LOGIN_DETAILS
import com.example.goldit.room.Alert
import androidx.lifecycle.AndroidViewModel
import com.example.goldit.login.EMAIL
import com.example.goldit.repository.*
import com.example.goldit.room.PRICE_DROP
import kotlinx.coroutines.launch

class SetAlertViewModel(application: Application, private val dataSource: DataSource) :
    AndroidViewModel(application) {

    private val _notificationError = MutableLiveData<Boolean>()
    val notificationError: LiveData<Boolean>
        get() = _notificationError

    private val _saveButtonClicked = MutableLiveData<Boolean>()
    val saveButton: LiveData<Boolean>
        get() = _saveButtonClicked

    private val _alertDeleted = MutableLiveData<Boolean>()
    val alertDeleted: LiveData<Boolean>
        get() = _alertDeleted

    var price = 0
    var caratType: String = ""
    private var alert : Alert? = null

    private val sharedPreferences =
        getApplication<Application>().getSharedPreferences(LOGIN_DETAILS, Context.MODE_PRIVATE)
    val email = sharedPreferences?.getString(EMAIL, "").toString()

    private val _isPriceError = MutableLiveData<Boolean>()
    val isPriceError: LiveData<Boolean>
        get() = _isPriceError

    private val _isAlreadyAchieved = MutableLiveData<Boolean>()
    val isAlreadyAchieved: LiveData<Boolean>
        get() = _isAlreadyAchieved

    private val _isGoldTypeError = MutableLiveData<Boolean>()
    val isGoldTypeError: LiveData<Boolean>
        get() = _isGoldTypeError

    private val _alertCountError = MutableLiveData<Boolean>()
    val alertCountError: LiveData<Boolean>
        get() = _alertCountError

    fun onPriceChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.length > 1) {
            price = s.toString().toInt()
        }
    }

    fun onSaveButtonClicked() {

        //Validate Price Drop

        val currentPriceData = dataSource.getGoldRateByType(caratType)
        var currentPrice = 0
        if (currentPriceData is Result.Success) {
            currentPrice = currentPriceData.data.goldRate
        }

        _isPriceError.value = price <= 0
        _isAlreadyAchieved.value = price >= currentPrice
        _isGoldTypeError.value = caratType.isNullOrEmpty()

        //Validate Alert Count
        validateAlertCount(email)

        _saveButtonClicked.value = _isPriceError.value == false &&
                _isGoldTypeError.value == false && _alertCountError.value == false
            && _isAlreadyAchieved.value == false

    }

    fun validateAlertCount(userName: String) {

        val result = dataSource.getUserActiveAlertCount(userName)

        if (result is Result.Success) {
            _alertCountError.value = result.data > 10
        } else {
            _alertCountError.value = true
        }

    }

    fun onDeleteClicked(alert: Alert) {
        viewModelScope.launch {
            dataSource.deleteAlert(alert.id)
        }
        _alertDeleted.value = true
    }

    fun onDeletedAndNavigatedToMainPage() {
        clearData()
        _alertDeleted.value = false
    }

    fun saveAlert(goldType: String?, price: Int, userName: String) {
        val alert =
            Alert(goldType.toString().trim(), price, false, userName, PRICE_DROP, 0)
        viewModelScope.launch {
            dataSource.addAlert(alert)
        }

    }

    fun updateAlert(alertId: Long, goldType: String?, price: Int, userName: String) {
        val alert = Alert(
            goldType.toString().trim(),
            price,
            false,
            userName,
            PRICE_DROP,
            0,
            alertId
        )

        viewModelScope.launch {
            dataSource.updateAlert(alert)
        }


    }

    fun getAlert(id: Long, userName: String): Alert? {

        val alertDetails = dataSource.getActiveAlertById(id, userName)
        return if (alertDetails is Result.Success) {

            if (alert?.id != alertDetails.data.id) {
                price = alertDetails.data.goldRate
                caratType = alertDetails.data.goldType
            }

            alert = alertDetails.data
            alertDetails.data

        } else {
            null
        }

    }

    fun setNotificationError(status: Boolean) {
        _notificationError.value = status
    }

    fun setGoldType(goldType: String) {
        caratType = goldType
    }

    fun navigatedToMainPage() {
        clearData()
        _saveButtonClicked.value = false
    }

    fun clearData() {
        caratType = ""
        price = 0
        _isAlreadyAchieved.value = false
        _isGoldTypeError.value = false
        _isPriceError.value = false
        _alertCountError.value = false
        _saveButtonClicked.value = false
    }

    init {
        Log.i("ViewModel", "Set Alert ViewModel Initialized")
    }

}