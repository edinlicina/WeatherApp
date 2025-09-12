package com.example.weatherapp

import kotlinx.serialization.Serializable

@Serializable
data class GeoLocationResponse(
    val name: String,
    val lat: Float,
    val lon: Float,
    val country: String
)