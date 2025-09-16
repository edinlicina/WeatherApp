package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings ORDER BY id DESC LIMIT 1")
    fun getLatestSettings(): Flow<SettingsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(item: SettingsEntity)

}