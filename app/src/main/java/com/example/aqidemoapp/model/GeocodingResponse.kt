package com.example.aqidemoapp.model

data class GeocodingResponse(
    val results: List<Result>,
    val status: Status
)

data class Result(
    val geometry: Geometry
)

data class Geometry(
    val lat: Double,
    val lng: Double
)

data class Status(
    val code: Int,
    val message: String
)

