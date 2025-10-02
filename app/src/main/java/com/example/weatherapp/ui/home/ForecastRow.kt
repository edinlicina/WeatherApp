package com.example.weatherapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.local.ForecastEntity
import com.example.weatherapp.ui.getWeatherIconRes

@Composable
fun ForecastRow(
    item: ForecastEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconCode = item.iconCode
    val desc     = item.description
    val tempC    = "${"%.1f".format(item.temperature)} °C"
    val dateTxt  = item.dateTime

    Row(
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(getWeatherIconRes(iconCode)),
            contentDescription = desc,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(dateTxt, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            Text(
                text = desc.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(tempC, style = MaterialTheme.typography.titleMedium)
    }
}