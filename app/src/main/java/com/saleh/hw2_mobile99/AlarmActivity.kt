package com.saleh.hw2_mobile99

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.math.abs


class AlarmActivity : AppCompatActivity() {

    private lateinit var vibrator: Vibrator
    private lateinit var sensorManager: SensorManager


    var myJob = Job()
    var vibrateTime = 1000L * 60 * 10

    private val gyroSensorListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {

            if (abs(sensorEvent.values[2]) >= DataHolders.gyroSpeedThreshold) {
                if (!myJob.isCancelled) {
                    Timber.i("${sensorEvent.values[0]}  ${sensorEvent.values[1]}  ${sensorEvent.values[2]}")
                    cancelVibrate()
                    DataHolders.setIsAlarmPending(this@AlarmActivity, AlarmState.NOT_SET)
                    myJob.cancel()
                    finish()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_activity)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        }

        DataHolders.updateValue(this)


        if (DataHolders.alarmPending == AlarmState.WAIT_FOR_STOP) {
            startVibrate()
            myJob = Job()
            CoroutineScope(Dispatchers.Main + myJob).launch {
                withContext(Dispatchers.Default) {
                    delay(vibrateTime)
                }
                cancelVibrate()
                DataHolders.setIsAlarmPending(this@AlarmActivity, AlarmState.NOT_SET)
                finish()
            }
        } else {
            finish()
        }

        initializeSensor()

    }


    override fun onBackPressed() {

    }

    override fun onPause() {
        super.onPause()
        if (DataHolders.alarmPending == AlarmState.WAIT_FOR_STOP) {
            finish()
            val i = Intent(this, AlarmActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this.applicationContext.startActivity(i)
        } else {
            sensorManager.unregisterListener(gyroSensorListener)
        }
    }


    private fun initializeSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)


        sensorManager.registerListener(gyroSensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun startVibrate() {

        try {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(0, 1000, 450)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                vibrator.vibrate(pattern, 0)
            }
        } catch (ex: Exception) {
            Timber.wtf(ex)
        }
    }

    private fun cancelVibrate() {
        Timber.i(vibrateTime.toString())
        try {
            vibrator.cancel()
        } catch (ex: Exception) {
            Timber.wtf(ex)
        }
    }


}
