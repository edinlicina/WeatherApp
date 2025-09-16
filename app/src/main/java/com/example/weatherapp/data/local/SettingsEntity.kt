package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 0,
    val cityName: String = "Vienna",
    val cityLat: Float = 48.2083537f,
    val cityLon: Float = 16.3725042f
)
