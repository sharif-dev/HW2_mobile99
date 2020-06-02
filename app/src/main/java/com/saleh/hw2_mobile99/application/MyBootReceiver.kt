package com.saleh.hw2_mobile99.application

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.saleh.hw2_mobile99.*
import com.saleh.hw2_mobile99.alarmManager.AlarmManaging
import timber.log.Timber

class MyBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Timber.i("Boot Received")

            DataHolders.updateValue(context)
            DataHolders.printState()
            if (DataHolders.alarmPending == AlarmState.PENDING) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                AlarmManaging.initMe(alarmManager)
                AlarmManaging.setAlarm("ALARM_ACTION", context, DataHolders.alarmTime)
            }
        }
    }

}

