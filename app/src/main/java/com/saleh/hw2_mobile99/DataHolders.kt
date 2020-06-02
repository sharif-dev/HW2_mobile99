package com.saleh.hw2_mobile99

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import timber.log.Timber

enum class AlarmState(val value: Int) {
    NOT_SET(0),
    PENDING(1),
    WAIT_FOR_STOP(2)
}

object DataHolders {
    var gyroSpeedThreshold: Int = 5
        private set
    var alarmTime: String = "00:00"
        private set
    var alarmEnabled: Boolean = true
        private set
    var alarmPending: AlarmState = AlarmState.NOT_SET
        private set

    fun updateValue(context: Context) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        alarmEnabled = prefs.getBoolean(context.getString(R.string.alarmEnabledKey), false)
        alarmTime = prefs.getString(context.getString(R.string.alarmTimeKey), alarmTime) ?: alarmTime
        gyroSpeedThreshold = prefs.getString(
            context.getString(R.string.alarmGyroThreshold),
            gyroSpeedThreshold.toString()
        )?.toIntOrNull()
            ?: gyroSpeedThreshold
        alarmPending = getIsAlarmPending(context.applicationContext)
    }

    fun setIsAlarmPending(context: Context, state: AlarmState) {
        val prefs: SharedPreferences = context.applicationContext.getSharedPreferences("alarmDetails", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(context.getString(R.string.alarmPending), state.name)
        editor.commit()
        alarmPending = state
    }


    fun getIsAlarmPending(context: Context): AlarmState {
        val prefs: SharedPreferences = context.applicationContext.getSharedPreferences("alarmDetails", MODE_PRIVATE)
        if (!prefs.contains(context.getString(R.string.alarmPending))) {
            setIsAlarmPending(context, AlarmState.NOT_SET)
            return AlarmState.NOT_SET
        }
        return AlarmState.valueOf(prefs.getString(context.getString(R.string.alarmPending), null) ?: AlarmState.NOT_SET.name)
    }

    fun printState() {
        Timber.i("enable=$alarmEnabled , pending=$alarmPending , time=$alarmTime , gyro=$gyroSpeedThreshold")
    }
}