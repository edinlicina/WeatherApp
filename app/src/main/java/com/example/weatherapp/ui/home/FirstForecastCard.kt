package com.example.weatherapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.local.ForecastEntity
import com.example.weatherapp.ui.getWeatherIconRes

@Composable
fun FirstForecastCard(
    item: ForecastEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconCode = item.iconCode
    val desc = item.description
    val tempC = "${"%.1f".format(item.temperature)} °C"
    val dateTxt = item.dateTime

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(dateTxt, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Image(
                painter = painterResource(getWeatherIconRes(iconCode)),
                contentDescription = desc,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = desc.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(tempC, style = MaterialTheme.typography.headlineSmall)
        }
    }
}