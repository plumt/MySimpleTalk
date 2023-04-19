package com.yun.mysimpletalk

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MySimpleTalkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}