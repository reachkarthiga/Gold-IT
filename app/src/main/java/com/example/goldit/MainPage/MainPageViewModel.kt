package com.example.goldit.MainPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goldit.R

const val HOME_PAGE = R.id.home
const val NOTIFIED_ALERT_PAGE = R.id.notifiedAlert

class MainPageViewModel: ViewModel() {

    private var _displayPage = MutableLiveData<Int>()
    val displayPage : LiveData<Int>
        get() = _displayPage


    init {
        setHome()
    }

    fun setHome(){
        _displayPage.value = HOME_PAGE
    }

    fun setNotifiedAlertPage(){
        _displayPage.value = R.id.notifiedAlert
    }

}