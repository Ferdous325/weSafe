package com.bauet.wesafe.utils

import android.app.Application
import com.bauet.wesafe.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
        startKoin {
            androidContext(this@MainApplication)
            modules(listOf(appModule))
        }
    }
}