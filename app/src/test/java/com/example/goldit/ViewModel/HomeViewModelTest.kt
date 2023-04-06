package com.example.goldit.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.goldit.Data.FakeDataSource
import com.example.goldit.MainCoroutineRule
import com.example.goldit.getOrAwaitValue
import com.example.goldit.home.HomeViewModel
import com.example.goldit.repository.carat22
import com.example.goldit.repository.carat24
import com.example.goldit.room.Alert
import com.example.goldit.room.LiveRate
import com.example.goldit.room.PRICE_DROP
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var dataSource:FakeDataSource
    private lateinit var homeViewModel: HomeViewModel

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initialSetup() {


        dataSource = FakeDataSource()
        homeViewModel = HomeViewModel(dataSource, ApplicationProvider.getApplicationContext())
    }

    @Test
    fun addNewClicked() {
        homeViewModel.addNewClicked()
        assertThat(homeViewModel.addNewAlert.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun navigatedToSetAlert() {
        homeViewModel.navigatedToSetAlert()
        assertThat(homeViewModel.addNewAlert.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun userNameClicked() {
        homeViewModel.userNameClicked()
        assertThat(homeViewModel.userNameClicked.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun navigatedToProfileDetails() {
        homeViewModel.navigatedToProfileDetails()
        assertThat(homeViewModel.userNameClicked.getOrAwaitValue(), `is`(false))
    }


}