package com.example.goldit.Data

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.goldit.getOrAwaitValue
import com.example.goldit.repository.Result
import com.example.goldit.repository.carat22
import com.example.goldit.repository.carat24
import com.example.goldit.room.Alert
import com.example.goldit.room.DataBase
import com.example.goldit.room.PRICE_DROP
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class AlertDaoTest {

    private lateinit var database: DataBase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initialSetup() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), DataBase::class.java).build()
    }

    @After
    fun clearDatabase() {
        database.close()
    }

    @Test
    fun saveAlert_getActiveAlertById() = runBlockingTest{

        val price = 1234
        val userName = "Testing"
        val goldType = carat24

        database.alertDao.saveAlert(Alert(goldType, price, false, userName, PRICE_DROP, 0, 1))

        val result = database.alertDao.getActiveAlertById(1, userName)

            assertThat(result.alertAchieved, `is`(false))
            assertThat(result.achievedDateTime, `is`(0))
            assertThat(result.alertType, `is`(PRICE_DROP))
            assertThat(result.userEmail, `is`(userName))
            assertThat(result.goldRate, `is`(price))
            assertThat(result.goldType, `is`(carat24))
   }


    @Test
    fun updateAlert() = runBlockingTest {

        var price = 1234
        val userName = "Testing"
        var goldType = carat24

        database.alertDao.saveAlert(Alert(goldType, price, false, userName, PRICE_DROP, 0, 1))

        goldType = carat22
        price = 5678

        database.alertDao.updateAlert(Alert(goldType, price, false, userName, PRICE_DROP, 0, 1))

        val result = database.alertDao.getActiveAlertById(1, userName)

        assertThat(result.goldRate, `is`(price))
        assertThat(result.goldType, `is`(goldType))
    }

    @Test
    fun deleteAlert() = runBlockingTest{

        val price = 1234
        val userName = "Testing"
        val goldType = carat24

        database.alertDao.saveAlert(Alert(goldType, price, false, userName, PRICE_DROP, 0, 1))

        database.alertDao.deleteAlert(1)

        val result = database.alertDao.getActiveAlertById(1, userName)

        assertThat(result, `is`(nullValue()))

    }

    @Test
    fun getNotifiedAlertById() = runBlockingTest {
        val price = 1234
        val userName = "Testing"
        val goldType = carat24

        database.alertDao.saveAlert(Alert(goldType, price, false, userName, PRICE_DROP, 0, 1))
        database.alertDao.saveAlert(Alert(goldType, price, true, userName, PRICE_DROP, 1234, 2))

        val result = database.alertDao.getNotifiedAlertById(2, userName)

        assertThat(result.alertAchieved , `is`(true))
        assertThat(result.achievedDateTime , `is`(not(0)))

    }

    @Test
    fun getUserActiveAlertCount()= runBlockingTest {
        val price = 1234
        val userName = "Testing"
        val goldType = carat24

        database.alertDao.saveAlert(Alert(goldType, price, false, userName, PRICE_DROP, 0, 1))
        database.alertDao.saveAlert(Alert(goldType, price, true, userName, PRICE_DROP, 1234, 2))
        database.alertDao.saveAlert(Alert(goldType, price, false, userName, PRICE_DROP, 0, 3))

        val result = database.alertDao.getUserActiveAlertCount(userName)

        assertThat(result, `is`(2))

    }

    @Test
    fun enableAlert() = runBlockingTest {
        val price = 1234
        val userName = "Testing"
        val goldType = carat24

        database.alertDao.saveAlert(Alert(goldType, price, true, userName, PRICE_DROP, 1234, 1))
        database.alertDao.saveAlert(Alert(goldType, price, true, userName, PRICE_DROP, 123456, 3))

        database.alertDao.enableAlert(3)

        var result = database.alertDao.getActiveAlertById(3, userName)

        assertThat(result.achievedDateTime , `is`(0))
        assertThat(result.alertAchieved, `is`(false))

        result = database.alertDao.getNotifiedAlertById(1, userName)

        assertThat(result.achievedDateTime , `is`(not(0)))
        assertThat(result.alertAchieved, `is`(true))

    }

    @Test
    fun checkIfAlertAchieved() = runBlockingTest {

        val userName = "Testing"
        val goldType = carat24
        val price = 3421

        database.alertDao.saveAlert(Alert(goldType, 3425, false, userName, PRICE_DROP, 0, 1))
        database.alertDao.saveAlert(Alert(goldType, 123, false, userName, PRICE_DROP, 0, 3))
        database.alertDao.saveAlert(Alert(goldType, 7876, true, userName, PRICE_DROP, 234578, 2))

        val result =  database.alertDao.checkIfAlertAchieved(price, goldType, userName)

        assertThat(result[0].id , `is`(1))
        assertThat(result[0].goldRate , `is`(3425))
        assertThat(result[0].alertAchieved, `is`(false))
        assertThat(result.size, `is`(1))

    }

    @Test
    fun getActiveAlerts() = runBlockingTest{

        val userName = "Testing"

        database.alertDao.saveAlert(Alert(carat24, 3425, false, userName, PRICE_DROP, 0, 1))
        database.alertDao.saveAlert(Alert(carat22, 123, false, userName, PRICE_DROP, 0, 3))
        database.alertDao.saveAlert(Alert(carat24, 1233, false, userName, PRICE_DROP, 0, 5))
        database.alertDao.saveAlert(Alert(carat24, 7876, true, userName, PRICE_DROP, 234578, 2))
        database.alertDao.saveAlert(Alert(carat22, 434, true, userName, PRICE_DROP, 234578, 4))

        val data = database.alertDao.getActiveAlerts(userName).getOrAwaitValue()
        assertThat(data.size , `is`(3))

    }

    @Test
    fun getNotifiedAlerts() = runTest{

        val userName = "Testing"

        database.alertDao.saveAlert(Alert(carat24, 3425, false, userName, PRICE_DROP, 0, 1))
        database.alertDao.saveAlert(Alert(carat22, 123, false, userName, PRICE_DROP, 0, 3))
        database.alertDao.saveAlert(Alert(carat24, 1233, false, userName, PRICE_DROP, 0, 5))
        database.alertDao.saveAlert(Alert(carat24, 7876, true, userName, PRICE_DROP, 234578, 2))
        database.alertDao.saveAlert(Alert(carat22, 434, true, userName, PRICE_DROP, 234578, 4))

        val data = database.alertDao.getNotifiedAlerts(userName).getOrAwaitValue()
        assertThat(data.size , `is`(2))


    }



}