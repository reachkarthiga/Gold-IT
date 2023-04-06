package com.example.goldit.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.goldit.Data.FakeDataSource
import com.example.goldit.MainCoroutineRule
import com.example.goldit.getOrAwaitValue
import com.example.goldit.notifiedAlerts.NotifiedAlertViewModel
import com.example.goldit.repository.carat24
import com.example.goldit.room.Alert
import com.example.goldit.room.LiveRate
import com.example.goldit.room.PRICE_DROP
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class NotifiedAlertViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var notifiedAlertViewModel: NotifiedAlertViewModel

    @Before
    fun initialSetUp() {
        stopKoin()
        dataSource = FakeDataSource()
        notifiedAlertViewModel = NotifiedAlertViewModel(
            dataSource, ApplicationProvider.getApplicationContext()
        )
    }

    @Test
    fun getAlertDetails() = mainCoroutineRule.runBlockingTest {

        val userName = "Testing"
        val alert = Alert(carat24, 1234, true, userName, PRICE_DROP, 0)
        dataSource.addAlert(alert)

        val result = notifiedAlertViewModel.getAlertDetails(dataSource.alertList[0].id, userName)

        assertThat(result, `is`(alert))

    }

    @Test
    fun enableAlert_countError() {

        val userName = "Testing"
        val alert = Alert(carat24, 1234, false, userName, PRICE_DROP, 123)

        for (i in 0..10) {
            Thread.sleep(1)
            dataSource.alertList.add(alert)
        }

        notifiedAlertViewModel.enableAlert(dataSource.alertList[0])

        assertThat(notifiedAlertViewModel.alertCountError.getOrAwaitValue(), `is`(true))
        assertThat(notifiedAlertViewModel.moveToListPage.getOrAwaitValue(), `is`(false))

    }

    @Test
    fun enableAlert_NoError() {

        val userName = "Testing"
        val alert = Alert(carat24, 1234, false, userName, PRICE_DROP, 0)

        dataSource.alertList.add(alert)

        notifiedAlertViewModel.enableAlert(dataSource.alertList[0])

        assertThat(notifiedAlertViewModel.alertCountError.getOrAwaitValue(), `is`(false))
        assertThat(notifiedAlertViewModel.moveToListPage.getOrAwaitValue(), `is`(true))

        val result = dataSource.alertList[0]

        assertThat(result.achievedDateTime, `is`(0))
        assertThat(result.alertAchieved, `is`(false))

    }

    @Test
    fun getLiveRate_NoError() {
        val liveRate = LiveRate(carat24, 1234)
        dataSource.ratesList.add(liveRate)

        val result = notifiedAlertViewModel.getLiveRate(carat24)

        assertThat(liveRate, `is`(result))

    }

    @Test
    fun movedToListPage_returnFalse() {
        notifiedAlertViewModel.movedToListPage()
        assertThat(notifiedAlertViewModel.moveToListPage.getOrAwaitValue(), `is`(false))
    }


}