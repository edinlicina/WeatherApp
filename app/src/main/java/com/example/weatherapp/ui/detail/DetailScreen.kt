package com.example.weatherapp.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.ui.getWeatherIconRes

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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Image(
                painter = painterResource(id = getWeatherIconRes(icon)),
                contentDescription = condition,
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
            )


            Text("Condition: $condition", style = MaterialTheme.typography.bodyLarge)
            Text("Date and Time: $dateTime", style = MaterialTheme.typography.bodyLarge)
            Text("Temperature: $temperature °C", style = MaterialTheme.typography.bodyLarge)
            Text("Pressure: $pressure hPa", style = MaterialTheme.typography.bodyLarge)
            Text("Humidity: $humidity %", style = MaterialTheme.typography.bodyLarge)
            Text("Cloud cover: $cloudCover %", style = MaterialTheme.typography.bodyLarge)
            Text("Wind speed: $windSpeed m/s", style = MaterialTheme.typography.bodyLarge)
            Text("Wind direction: $windDirection°", style = MaterialTheme.typography.bodyLarge)
            Text("Rain (last 3h): $rain", style = MaterialTheme.typography.bodyLarge)
            Text("Snow (last 3h): $snow", style = MaterialTheme.typography.bodyLarge)
        }
    }
}