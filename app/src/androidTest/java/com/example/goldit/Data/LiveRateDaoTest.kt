package com.example.goldit.Data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.goldit.getOrAwaitValue
import com.example.goldit.repository.Repository
import com.example.goldit.repository.carat22
import com.example.goldit.repository.carat24
import com.example.goldit.room.DataBase
import com.example.goldit.room.LiveRate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class LiveRateDaoTest {

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
    fun insertLiveRate_getLiveRate() = runTest{
        database.liveRateDao.insertLiveRate(LiveRate(carat24, 1234, 1))
        database.liveRateDao.insertLiveRate(LiveRate(carat22, 1234, 2))

        val result = database.liveRateDao.getLiveRate().getOrAwaitValue()

        assertThat(result.size, `is`(2))

    }

    @Test
    fun getGoldRateByType() = runTest{
        database.liveRateDao.insertLiveRate(LiveRate(carat24, 1234, 1))
        database.liveRateDao.insertLiveRate(LiveRate(carat22, 124, 2))

        val result = database.liveRateDao.getGoldRateByType(carat22)

        assertThat(result.goldType, `is`(carat22))
        assertThat(result.goldRate, `is`(124))

    }

    @Test
    fun deletePreviousRates() = runTest {

        database.liveRateDao.insertLiveRate(LiveRate(carat24, 1234, 1))
        database.liveRateDao.insertLiveRate(LiveRate(carat22, 124, 2))

        database.liveRateDao.deletePreviousRates()

        val result = database.liveRateDao.getLiveRate().getOrAwaitValue()

        assertThat(result.size, `is`(0))

    }

}