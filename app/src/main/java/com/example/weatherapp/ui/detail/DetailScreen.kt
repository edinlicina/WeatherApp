package com.example.weatherapp.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun DetailScreen(
    navController: NavController,
    icon: String,
    condition: String,
    dateTime: String,
    temperature: String,
    pressure: String,
    humidity: String,
    cloudCover: String,
    windSpeed: String,
    windDirection: String,
    rain: String,
    snow: String,
) {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(
                text = "Icon: $icon",
            )
            Text(
                text = "Condition: $condition",
            )
            Text(
                text = "Date and Time: $dateTime",
            )
            Text(
                text = "Temperature: $temperature",
            )
            Text(
                text = "Pressure: $pressure",
            )
            Text(
                text = "Humidity: $humidity",
            )
            Text(
                text = "Cloud cover: $cloudCover",
            )
            Text(
                text = "Wind speed: $windSpeed",
            )
            Text(
                text = "Wind direction: $windDirection",
            )
            Text(
                text = "Rain (last 3h): $rain",
            )
            Text(
                text = "Snow (last 3h): $snow",
            )
        }
    }
}
