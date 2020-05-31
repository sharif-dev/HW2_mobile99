package com.saleh.hw2_mobile99

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import timber.log.Timber

object DataHolders {
    var gyroSpeedThreshold: Int = 10
    var alarmTime: String = "00:00"
    var alarmEnabled: Boolean = true

    fun updateValue(context: Context) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        alarmEnabled = prefs.getBoolean(context.getString(R.string.alarmEnabledKey), alarmEnabled)
        alarmTime = prefs.getString(context.getString(R.string.alarmTimeKey), alarmTime) ?: alarmTime
        gyroSpeedThreshold = prefs.getString(context.getString(R.string.alarmGyroThreshold), gyroSpeedThreshold.toString())?.toIntOrNull()
            ?: gyroSpeedThreshold
        Timber.i("$alarmEnabled , $gyroSpeedThreshold , $alarmTime")
    }


}