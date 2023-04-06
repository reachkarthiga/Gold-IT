package com.example.goldit.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.goldit.Data.FakeDataSource
import com.example.goldit.MainCoroutineRule
import com.example.goldit.MainPage.HOME_PAGE
import com.example.goldit.MainPage.MainPageViewModel
import com.example.goldit.MainPage.NOTIFIED_ALERT_PAGE
import com.example.goldit.getOrAwaitValue
import com.example.goldit.setAlert.SetAlertViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class MainPageViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var mainPageViewModel: MainPageViewModel

    @Before
    fun initialSetUp() {
        stopKoin()
        mainPageViewModel = MainPageViewModel()
    }

    @Test
    fun initialPageCheck_toDisplayHome() {
        assertThat(mainPageViewModel.displayPage.getOrAwaitValue(), `is`(HOME_PAGE))
    }

    @Test
    fun setHome_toDisplayHome(){
        mainPageViewModel.setHome()
        assertThat(mainPageViewModel.displayPage.getOrAwaitValue(), `is`(HOME_PAGE))
    }

    @Test
    fun setNotifiedAlertPage_toDisplayAlerts(){
        mainPageViewModel.setNotifiedAlertPage()
        assertThat(mainPageViewModel.displayPage.getOrAwaitValue(), `is`(NOTIFIED_ALERT_PAGE))
    }



}