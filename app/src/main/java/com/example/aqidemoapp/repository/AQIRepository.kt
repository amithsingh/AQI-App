package com.example.aqidemoapp.repository

import com.example.aqidemoapp.model.AQIResponse
import com.example.aqidemoapp.model.GeocodingResponse
import com.example.aqidemoapp.remote.AQIApiService
import com.example.aqidemoapp.remote.GeocodingApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import com.example.aqidemoapp.utils.Result


@Singleton
class AQIRepository @Inject constructor(
     private val apiService: AQIApiService,
     private val geocodingApiService: GeocodingApiService
) {
    fun getAQI(lat: Double, lon: Double, token: String): Flow<Result<AQIResponse>> = flow {
        try {
            val response = apiService.getAQI(lat, lon, token)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(Exception("No data")))
            } else {
                emit(Result.Failure(Exception("Failed to fetch AQI data")))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getCoordinates(city: String, apiKey: String): Flow<Result<GeocodingResponse>> = flow {
        try {
            val response = geocodingApiService.getCoordinates(city, apiKey)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(Exception("No data")))
            } else {
                emit(Result.Failure(Exception("Failed to fetch coordinates")))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}
