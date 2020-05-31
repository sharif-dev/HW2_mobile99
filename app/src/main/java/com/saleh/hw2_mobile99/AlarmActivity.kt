package com.saleh.hw2_mobile99

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import timber.log.Timber


class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_activity)

        DataHolders.updateValue(this)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val gyroSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {

                if (sensorEvent.values[2] >= 8.0) {
                    Timber.i("${sensorEvent.values[0]}  ${sensorEvent.values[1]}  ${sensorEvent.values[2]}")
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        sensorManager.registerListener(gyroSensorListener, sensor, SensorManager.SENSOR_DELAY_GAME)
        Timber.i(DataHolders.gyroSpeedThreshold.toString())
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        Timber.i(prefs.getString("alarm_time","d"))
    }
}