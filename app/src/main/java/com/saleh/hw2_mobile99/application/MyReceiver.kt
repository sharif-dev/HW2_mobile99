package com.saleh.hw2_mobile99.application

import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.saleh.hw2_mobile99.*
import timber.log.Timber


class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ALARM_ACTION") {
            val i = Intent(context, AlarmActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            DataHolders.updateValue(context)
            DataHolders.setIsAlarmPending(context, AlarmState.WAIT_FOR_STOP)
            Timber.i("Received :")
            DataHolders.printState()
            context.applicationContext.startActivity(i)
        }
        Timber.i("Intent Received")
    }
}

