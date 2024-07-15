package com.example.aqidemoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aqidemoapp.model.AQIData
import com.example.aqidemoapp.model.AQIResponse
import com.example.aqidemoapp.model.City
import com.example.aqidemoapp.model.GeocodingResponse
import com.example.aqidemoapp.model.Geometry
import com.example.aqidemoapp.model.Status
import com.example.aqidemoapp.model.Time
import com.example.aqidemoapp.repository.AQIRepository
import com.example.aqidemoapp.utils.Constants
import com.example.aqidemoapp.viewmodel.AQIViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.Date
import com.example.aqidemoapp.utils.Result

@ExperimentalCoroutinesApi
class AQIViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()


    private lateinit var viewModel: AQIViewModel
    private lateinit var repository: AQIRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository= mockk(relaxed = true)
        viewModel = AQIViewModel(repository)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchCoordinates should update geocodingData and aqiData on success`() = runTest {
        val city = "New York"
        val lat = 40.7128
        val lon = -74.0060
        val geocodingResponse = GeocodingResponse(
            results = listOf(
                com.example.aqidemoapp.model.Result(
                    geometry = Geometry(
                        lat = lat,
                        lng = lon
                    )
                )
            ),
            status = Status(200, "OK")
        )
        val aqiResponse = AQIResponse(
            status = "ok",
            data = AQIData(
                aqi = 50,
                city = City(
                    name = "New York"
                ),
                time = Time(
                    s = Date().toString()
                )
            )
        )

        coEvery { repository.getCoordinates(city, Constants.geoCodingAPIKey) } returns flow {
            emit(Result.Success(geocodingResponse))
        }

        coEvery { repository.getAQI(lat, lon, Constants.AQIKey) } returns flow {
            emit(Result.Success(aqiResponse))
        }

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.fetchCoordinates(city)

        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.getCoordinates(city, Constants.geoCodingAPIKey) }
        coVerify { repository.getAQI(lat, lon, Constants.AQIKey) }

        assertEquals(Result.Success(geocodingResponse), viewModel.geocodingData.value)
        assertEquals(Result.Success(aqiResponse), viewModel.aqiData.value)
        assertEquals(false, viewModel.loading.value)
    }

    @Test
    fun `fetchAQI should update aqiData on success`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val aqiResponse = AQIResponse(
            status = "ok",
            data = AQIData(
                aqi = 50,
                city = City(
                    name = "New York"
                ),
                time = Time(
                    s = Date().toString()
                )
            )
        )

        coEvery { repository.getAQI(lat, lon, Constants.AQIKey) } returns flow {
            emit(Result.Success(aqiResponse))
        }

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.fetchAQI(lat, lon)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.getAQI(lat, lon, Constants.AQIKey) }

        assertEquals(Result.Success(aqiResponse), viewModel.aqiData.value)
        assertEquals(false, viewModel.loading.value)
    }


    @Test
    fun `fetchCoordinates should update loading and geocodingData on failure`() = runTest {
        val city = "InvalidCity"
        val error = Exception("Failed to fetch coordinates")

        coEvery { repository.getCoordinates(city, Constants.geoCodingAPIKey) } returns flow {
            emit(Result.Failure(error))
        }
        viewModel.fetchCoordinates(city)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.getCoordinates(city, Constants.geoCodingAPIKey) }
        assertEquals(Result.Failure(error), viewModel.geocodingData.value)
        assertEquals(null, viewModel.aqiData.value)
        assertEquals(false, viewModel.loading.value)
    }


    @Test
    fun `fetchAQI should update loading and aqiData on failure`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val error = Exception("Failed to fetch AQI data")

        coEvery { repository.getAQI(lat, lon, Constants.AQIKey) } returns flow {
            emit(Result.Failure(error))
        }
        viewModel.fetchAQI(lat, lon)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.getAQI(lat, lon, Constants.AQIKey) }
        assertEquals(Result.Failure(error), viewModel.aqiData.value)
        assertEquals(false, viewModel.loading.value)
    }
}
