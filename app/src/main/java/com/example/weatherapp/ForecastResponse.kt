package com.example.weatherapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse (
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherDataResponse>,
    val city: CityResponse

)

@Serializable
data class WeatherDataResponse(
    val dt: Int,
    val visibility: Int,
    val pop: Float,
    val main: WeatherDataMainResponse,
    val weather: List<WeatherDataWeatherResponse>,
    val clouds: WeatherDataCloudResponse,
    val wind: WeatherDataWindResponse,
    val sys: WeatherDataSysResponse
)

@Serializable
data class WeatherDataMainResponse(
    val temp: Float,
    val feelsLike: Float,
    val tempMin: Float,
    val temp_max: Float,
    val pressure: Int,
    val seaLevel: Int,
    val grndLevel: Float,
    val humidity: Float,
    val tempKf: Float,
)

@Serializable
data class WeatherDataWeatherResponse(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class WeatherDataCloudResponse(
    val all: Int
)

@Serializable
data class WeatherDataWindResponse(
    val speed: Float,
    val deg: Int,
    val gust: Float
)

@Serializable
data class WeatherDataSysResponse(
    val pod: String
)

@Serializable
data class CityResponse(
    val id: Int,
    val name: String,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Int,
    val sunset: Int,
    val coord: CityCoordResponse
)

@Serializable
data class CityCoordResponse(
    val lat: Float,
    val lon: Float
)


