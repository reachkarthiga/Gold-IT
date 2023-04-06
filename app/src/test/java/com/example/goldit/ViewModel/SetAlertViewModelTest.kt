package com.example.goldit.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.goldit.Data.FakeDataSource
import com.example.goldit.MainCoroutineRule
import com.example.goldit.getOrAwaitValue
import com.example.goldit.repository.Result
import com.example.goldit.repository.carat22
import com.example.goldit.repository.carat24
import com.example.goldit.room.PRICE_DROP
import com.example.goldit.setAlert.SetAlertViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SetAlertViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var setAlertViewModel: SetAlertViewModel

    @Before
    fun initialSetUp() {
        stopKoin()
        dataSource = FakeDataSource()
        setAlertViewModel = SetAlertViewModel(
            ApplicationProvider.getApplicationContext(), dataSource)
    }

    @Test
    fun onDeletedAndNavigatedToMainPage() {
        setAlertViewModel.onDeletedAndNavigatedToMainPage()
        assertThat(setAlertViewModel.alertDeleted.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun saveAlert_NoError() = mainCoroutineRule.runBlockingTest {

        val goldType = carat24
        val price = 1234
        val userName = "Testing"

        setAlertViewModel.saveAlert(goldType, price, userName)

        val result = dataSource.alertList.get(0)

        assertThat(result.goldType , `is`(goldType))
        assertThat(result.goldRate, `is`(price))
        assertThat(result.alertType, `is`(PRICE_DROP))
        assertThat(result.achievedDateTime , `is`(0))
        assertThat(result.alertAchieved, `is`(false))
        assertThat(result.userEmail, `is`(userName))

    }

    @Test
    fun onDeleteClicked_NoError() = mainCoroutineRule.run {
        val goldType = carat24
        val price = 123
        setAlertViewModel.saveAlert(goldType, price, "Testing")

        val alertToDelete = dataSource.alertList.get(0)
        setAlertViewModel.onDeleteClicked(alertToDelete)

        assertThat(dataSource.alertList.size , `is` (0))
        assertThat(setAlertViewModel.alertDeleted.getOrAwaitValue(), `is`(true))

    }

    @Test
    fun getAlert_NoError() = mainCoroutineRule.runBlockingTest {
        val goldType = carat24
        val price = 1234
        val userName = "Testing"

        setAlertViewModel.saveAlert(goldType, price, userName)

        val savedAlert = dataSource.alertList.get(0)

        val result = setAlertViewModel.getAlert(savedAlert.id, userName)

        assertThat(savedAlert, `is`(result))
    }

    @Test
    fun getAlert_Error() = mainCoroutineRule.runBlockingTest {
        val goldType = carat24
        val price = 1234
        val userName = "Testing"

        setAlertViewModel.saveAlert(goldType, price, userName)

        val savedAlert = dataSource.alertList.get(0)
        dataSource.setReturnError(true)

        val result = dataSource.getActiveAlertById(savedAlert.id, userName)

        assertThat(result, `is`(Result.Error("Error")))
    }

    @Test
    fun updateAlert_NoError() = mainCoroutineRule.runBlockingTest {
        var goldType = carat24
        var price = 1234
        val userName = "Testing"

        setAlertViewModel.saveAlert(goldType, price,  userName )

        goldType = carat22
        price = 123

        setAlertViewModel.updateAlert(dataSource.alertList.get(0).id, goldType, price, userName)

        val result = dataSource.alertList.get(0)

        assertThat(result.goldType , `is`(goldType))
        assertThat(result.goldRate, `is`(price))
        assertThat(result.alertAchieved , `is`(false))
        assertThat(result.achievedDateTime, `is`(0))
        assertThat(result.alertType, `is`(PRICE_DROP))
        assertThat(result.userEmail, `is`(userName))

    }

    @Test
    fun onSaveButtonClicked_pricePurityError() {

        setAlertViewModel.onSaveButtonClicked()

        assertThat(setAlertViewModel.isPriceError.getOrAwaitValue() , `is`(true))
        assertThat(setAlertViewModel.isGoldTypeError.getOrAwaitValue(), `is`(true))
        assertThat(setAlertViewModel.saveButton.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun validateAlertCount_alertCountError() {

        setAlertViewModel.price = 123
        setAlertViewModel.caratType = carat22

        val userName = "Testing"

        for (i in 0..10) {
            setAlertViewModel.saveAlert(carat24, 1234, userName)
        }

        setAlertViewModel.validateAlertCount(userName)

        assertThat(setAlertViewModel.alertCountError.getOrAwaitValue(), `is`(true))

    }

    @Test
    fun validateAlertCount_NoError() {

        setAlertViewModel.price = 123
        setAlertViewModel.caratType = carat22

        val userName = "Testing"

        for (i in 1..10) {
            setAlertViewModel.saveAlert(carat24, 1234, userName)
        }

        setAlertViewModel.validateAlertCount(userName)

        assertThat(setAlertViewModel.alertCountError.getOrAwaitValue(), `is`(false))

    }

    @Test
    fun onSaveButtonClicked_NoError() {

        setAlertViewModel.price = 123
        setAlertViewModel.caratType = carat22

        setAlertViewModel.onSaveButtonClicked()

        assertThat(setAlertViewModel.isPriceError.getOrAwaitValue() , `is`(false))
        assertThat(setAlertViewModel.isGoldTypeError.getOrAwaitValue(), `is`(false))
        assertThat(setAlertViewModel.saveButton.getOrAwaitValue(), `is`(true))

    }

    @Test
    fun setGoldType_NoError() {
        setAlertViewModel.setGoldType(carat24)
        assertThat(setAlertViewModel.caratType, `is`(carat24))
    }

}