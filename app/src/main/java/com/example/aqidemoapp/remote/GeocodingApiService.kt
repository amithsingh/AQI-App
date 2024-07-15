package com.example.aqidemoapp.remote

import com.example.aqidemoapp.model.GeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {
    @GET("geocode/v1/json")
    suspend fun getCoordinates(
        @Query("q") city: String,
        @Query("key") apiKey: String
    ): Response<GeocodingResponse>
}
