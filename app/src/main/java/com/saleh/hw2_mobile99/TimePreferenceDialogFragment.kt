package com.saleh.hw2_mobile99

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TimePicker
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat

class TimePreferenceDialogFragment : PreferenceDialogFragmentCompat()
    {
    lateinit var timePicker: TimePicker

    override fun onCreateDialogView(context: Context?): View {
        timePicker = TimePicker(context)
        return timePicker
    }

    override fun onBindDialogView(v: View?) {
        super.onBindDialogView(v)
        timePicker.setIs24HourView(true)

        val pref = preference as TimePreference
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.hour = pref.hour
            timePicker.minute = pref.minute
        } else {
            timePicker.currentHour = pref.hour
            timePicker.currentMinute = pref.minute
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val pref = preference as TimePreference
            pref.hour =
                if (Build.VERSION.SDK_INT >= 23) timePicker.hour else timePicker.currentHour
            pref.minute =
                if (Build.VERSION.SDK_INT >= 23) timePicker.minute else timePicker.currentMinute
            val value =
                TimePreference.timeToString(
                    pref.hour,
                    pref.minute
                )
            if (pref.callChangeListener(value)) pref.persistStringValue(value)
            pref.setSummary("dfdfdf")
        }
    }


}
