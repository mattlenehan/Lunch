package com.example.lunch

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LunchApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Always use light theme for this app
        // NOTE: Don't use this line in Activity or else activity.onCreate() will be called multiple times
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}