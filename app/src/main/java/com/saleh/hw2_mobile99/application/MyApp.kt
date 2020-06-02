package com.saleh.hw2_mobile99.application

import android.app.Application
import com.saleh.hw2_mobile99.BuildConfig
import timber.log.Timber

class MyApp : Application() {
    val tagPrefix = "MyTagPrefix"
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return "$tagPrefix      #${super.createStackElementTag(element)}"
                }
            })
            Timber.plant(Timber.DebugTree())
        }
    }
}
