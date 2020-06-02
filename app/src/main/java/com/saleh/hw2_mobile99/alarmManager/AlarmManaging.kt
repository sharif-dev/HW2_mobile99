package com.saleh.hw2_mobile99.alarmManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.saleh.hw2_mobile99.AlarmState
import com.saleh.hw2_mobile99.DataHolders
import com.saleh.hw2_mobile99.application.MyReceiver
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


object AlarmManaging {
    private lateinit var alarmManager: AlarmManager

    const val debugTime = false

    fun initMe(manager: AlarmManager) {
        alarmManager = manager
    }

    fun setAlarm(action: String, context: Context, time: String) {
        val hour = time.split(":")[0].toInt()
        val min = time.split(":")[1].toInt()
        this.setAlarm(action, context, hour, min)
    }

    fun setAlarm(action: String, context: Context, hour: Int, min: Int) {
        check(this::alarmManager.isInitialized) { "Initialize Alarm Manager First" }
        if (!DataHolders.alarmEnabled) {
            return
        }
        if (DataHolders.alarmPending == AlarmState.PENDING) {
            cancelAlarm(action, context)
        }

        val alarmIntent = Intent(context, MyReceiver::class.java).let { intent ->
            intent.action = action
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        var today = true
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            if (!debugTime) {
                if (get(Calendar.HOUR_OF_DAY) > hour)
                    today = false
                if (get(Calendar.HOUR_OF_DAY) == hour && get(Calendar.MINUTE) >= min)
                    today = false
                if (!today) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, min)
                set(Calendar.SECOND, 0)
            } else {
                add(Calendar.SECOND, 10)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis, alarmIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis, alarmIntent
            )
        }
        DataHolders.setIsAlarmPending(context, AlarmState.PENDING)
        Toast.makeText(
            context,
            "Alarm Set For ${if (today) "Today" else "Tomorrow"} at  ${SimpleDateFormat.getTimeInstance().format(calendar.time)}",
            Toast.LENGTH_SHORT
        ).show()
    }


    fun cancelAlarm(action: String, context: Context, toast: Boolean = false) {
        check(this::alarmManager.isInitialized) { "Initialize Alarm Manager First" }
        if (action == "ALARM_ACTION") {
            if (DataHolders.alarmPending == AlarmState.PENDING) {
                val alarmIntent = Intent(context, MyReceiver::class.java).let { intent ->
                    intent.action = action
                    PendingIntent.getBroadcast(context, 0, intent, 0)
                }
                if (alarmIntent != null) {
                    alarmManager.cancel(alarmIntent)
                    Timber.i("Canceled")
                    if (toast) {
                        Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_SHORT).show()
                    }
                }
                DataHolders.setIsAlarmPending(context, AlarmState.NOT_SET)
            }
        }
    }


}