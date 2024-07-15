package com.example.aqidemoapp.remote

import com.example.aqidemoapp.model.AQIResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AQIApiService {
    @GET("feed/geo:{lat};{lon}/")
    suspend fun getAQI(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Query("token") token: String
    ): Response<AQIResponse>
}
