package com.example.weatherapp

data class WeatherEntry(val dateTime: String, val icon: String, val condition: Int, val temperature: Float, val pressure: Float, val humidity: Float, val cloudCover: Float, val windSpeed: Float, val windDirection: Float, val rain: Float, val snow: Float)
