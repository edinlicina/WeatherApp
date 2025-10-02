package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: String,
    val temperature: Float,
    val description: String,
    val iconCode: String,
    val pressure: Int,
    val humidity: Float,
    val cloudCover: Int,
    val windSpeed: Float,
    val windDirection: Int,
    val cityName: String,
    val unit: String
)
