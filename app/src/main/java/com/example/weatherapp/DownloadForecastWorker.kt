package com.example.weatherapp

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DownloadForecastWorker(appContext: Context, workerParams: WorkerParameters, private val forecastRepository: ForecastRepository) :
        CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        forecastRepository.clear() //Delete old data
        forecastRepository.refresh(force = true)
        return Result.success()
    }
}