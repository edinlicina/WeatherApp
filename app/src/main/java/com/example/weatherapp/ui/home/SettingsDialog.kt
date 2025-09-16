package com.example.weatherapp.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.GeoLocationViewModel

@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    geoLocationVm: GeoLocationViewModel = viewModel(factory = GeoLocationViewModel.Factory)
) {
    val settings by geoLocationVm.state.collectAsState()
    var location by remember { mutableStateOf("") }

    LaunchedEffect(settings) {
        settings?.let {
            location = it.cityName
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                settings?.let {
                    Text(
                        text = "Current location: ${it.cityName}",
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Center,
                    )
                    TextField(
                        value = location,
                        onValueChange = { value ->
                            location = value
                        }
                    )
                    Button(onClick = {
                        geoLocationVm.fetch(location)
                        onDismissRequest()
                    }) {
                        Text("Save")
                    }
                }

            }
        }
    }
}
