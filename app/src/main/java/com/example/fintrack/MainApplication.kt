package com.example.fintrack

import android.app.Application
import com.example.fintrack.data.Graph
import com.example.fintrack.data.OnBoardingRepository
import dagger.hilt.android.HiltAndroidApp

class MainApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        OnBoardingRepository.initialize(this)
        Graph.provide(this)
    }
}