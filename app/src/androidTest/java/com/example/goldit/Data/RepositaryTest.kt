package com.example.goldit.Data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.goldit.getOrAwaitValue
import com.example.goldit.repository.Repository
import com.example.goldit.repository.Result
import com.example.goldit.repository.carat22
import com.example.goldit.repository.carat24
import com.example.goldit.room.Alert
import com.example.goldit.room.DataBase
import com.example.goldit.room.LiveRate
import com.example.goldit.room.PRICE_DROP
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RepositoryTest {


    private lateinit var repository: Repository
    private lateinit var database: DataBase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initialSetup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DataBase::class.java
        ).build()
        repository = Repository(database)
    }

    @After
    fun clearDatabase() {
        database.close()
    }

    @Test
    fun addAlert_getAlertById() = runTest {

        val userName = "Testing"
        val alert = Alert(carat22, 1234, false, userName, PRICE_DROP, 0, 1)

        repository.addAlert(alert)

        val result = repository.getActiveAlertById(1, userName)

        assertThat(result as Result.Success<Alert>, `is`(Result.Success(alert)))

    }

    @Test
    fun updateAlert() = runTest {

        val userName = "Testing"
        val alert = Alert(carat24, 12345, false, userName, PRICE_DROP, 0, 1)

        repository.addAlert(Alert(carat22, 1234, false, userName, PRICE_DROP, 0, 1))
        repository.updateAlert(alert)

        val result = repository.getActiveAlertById(1, userName)

        assertThat(result as Result.Success<Alert>, `is`(Result.Success(alert)))

    }

    @Test
    fun deleteAlert() = runTest {

        val userName = "Testing"
        val alert = Alert(carat22, 1234, false, userName, PRICE_DROP, 0, 1)

        repository.addAlert(alert)
        repository.deleteAlert(1)

        val result = repository.getActiveAlertById(1, userName)

        assertThat(result as Result.Error, `is`(Result.Error("No Data Found")))

    }

    @Test
    fun getActiveAlerts() = runTest {

        val userName = "Testing"
        val alert = Alert(carat22, 1234, false, userName, PRICE_DROP, 0, 1)
        val alert1 = Alert(carat24, 12345, false, userName, PRICE_DROP, 0, 2)
        val alert2 = Alert(carat24, 12345, true, userName, PRICE_DROP, 1234567, 3)

        repository.addAlert(alert)
        repository.addAlert(alert1)
        repository.addAlert(alert2)

        val result = repository.getActiveAlerts(userName)

        assertThat((result.getOrAwaitValue() as Result.Success).data.size, `is`(2))
        assertThat((result.getOrAwaitValue() as Result.Success).data[0], `is`(alert1))
        assertThat((result.getOrAwaitValue() as Result.Success).data[1], `is`(alert))
    }

    @Test
    fun getNotifiedAlerts() = runTest {

        val userName = "Testing"
        val alert = Alert(carat22, 1234, false, userName, PRICE_DROP, 0, 1)
        val alert1 = Alert(carat24, 12345, false, userName, PRICE_DROP, 0, 2)
        val alert2 = Alert(carat24, 12345, true, userName, PRICE_DROP, 1234567, 3)

        repository.addAlert(alert)
        repository.addAlert(alert1)
        repository.addAlert(alert2)

        val result = repository.getNotifiedAlerts(userName)

        assertThat((result.getOrAwaitValue() as Result.Success).data.size, `is`(1))
        assertThat((result.getOrAwaitValue() as Result.Success).data[0], `is`(alert2))

    }

    @Test
    fun getNotifiedAlertById() = runTest {

        val userName = "Testing"
        val alert1 =
            Alert(carat24, 12345, false, userName, PRICE_DROP, 0, 2)
        val alert2 = Alert(carat24, 12345, true, userName, PRICE_DROP, 1234567, 3)

        repository.addAlert(alert1)
        repository.addAlert(alert2)

        val result = repository.getNotifiedAlertById(3, userName)

        assertThat((result as Result.Success).data , `is`(alert2))

    }

    @Test
    fun getUserActiveAlertCount() = runTest {

        val userName = "Testing"
        repository.addAlert(Alert(carat24, 12345, false, userName, PRICE_DROP, 0, 2))
        repository.addAlert(Alert(carat24, 12345, true, userName, PRICE_DROP, 2345, 3))
        repository.addAlert(Alert(carat24, 12345, false, userName, PRICE_DROP, 0, 4))

        val result = repository.getUserActiveAlertCount(userName)

        assertThat((result as Result.Success).data, `is`(2))
    }

    @Test
    fun enableAlert() = runTest {

        val userName = "Testing"
        repository.addAlert(Alert(carat24, 12345, false, userName, PRICE_DROP, 0, 2))
        repository.addAlert(Alert(carat24, 12345, true, userName, PRICE_DROP, 2345, 3))
        repository.addAlert(Alert(carat24, 12345, false, userName, PRICE_DROP, 0, 4))

        repository.enableAlert(3)

        val result = repository.getActiveAlertById(3, userName)

        assertThat((result as Result.Success).data.alertAchieved, `is`(false))
        assertThat((result).data.achievedDateTime, `is`(0))

    }

    @Test
    fun checkIfAlertAchieved()= runTest {

        val userName = "Testing"
        val alert = Alert(carat24, 3234, false, userName, PRICE_DROP, 0, 2)

        repository.addAlert(alert)
        repository.addAlert(Alert(carat24, 12345, true, userName, PRICE_DROP, 2345, 3))
        repository.addAlert(Alert(carat24, 123, false, userName, PRICE_DROP, 0, 4))

        val result = repository.checkIfAlertAchieved(3000, carat24, userName)

        assertThat((result as Result.Success).data, `is`(listOf(alert)))

    }

    @Test
    fun insertLiveRate_getGoldRateByType() = runTest {

        val liveRate = LiveRate(carat24, 123, 1)
        repository.insertLiveRate(liveRate)

        val result = repository.getGoldRateByType(carat24)

        assertThat((result as Result.Success).data, `is`(liveRate))

    }

    @Test
    fun deleteLiveRate() = runTest {

        repository.insertLiveRate(LiveRate(carat24, 123, 1))
        repository.insertLiveRate(LiveRate(carat22, 1234, 2))
        repository.insertLiveRate(LiveRate(carat22, 124, 3))

        repository.deletePreviousRates()

        val result = repository.getLiveRate().getOrAwaitValue()

        assertThat((result as Result.Success).data.size, `is`(0))

    }

    @Test
    fun getLiveRates() = runTest {

        val liveRate1 = LiveRate(carat22, 1234, 2)
        val liveRate2 = LiveRate(carat24, 123, 1)

        repository.insertLiveRate(liveRate2)
        repository.insertLiveRate(liveRate1)

        val result = repository.getLiveRate().getOrAwaitValue()

        assertThat((result as Result.Success).data.size, `is`(2))
        assertThat(result.data[0], `is`(liveRate1))
        assertThat(result.data[1], `is`(liveRate2))

    }





}