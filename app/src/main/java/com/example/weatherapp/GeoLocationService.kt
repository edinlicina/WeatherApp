package com.example.weatherapp

import retrofit2.http.GET
import retrofit2.http.Query

interface GeoLocationService {
    @GET("direct")
    suspend fun getLocation(@Query("q") q: String): List<GeoLocationResponse>
}