package com.example.aqidemoapp.model

data class AQIResponse(
    val status: String,
    val data: AQIData
)

data class AQIData(
    val aqi: Int,
    val city: City,
    val time: Time
)

data class City(
    val name: String
)

data class Time(
    val s: String
)

