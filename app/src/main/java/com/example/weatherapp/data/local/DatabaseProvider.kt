package com.example.weatherapp.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider{
    @Volatile private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "weather_db"
            ).build().also { INSTANCE = it }
        }
}