package com.saleh.hw2_mobile99

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.saleh.hw2_mobile99.application.MyReceiver
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var alarmManager: AlarmManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val numberPreference: EditTextPreference? = findPreference("gyro_thresh")
        numberPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        DataHolders.updateValue(requireActivity())
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == null)
            return

        Timber.i(key)

        val preference: Preference = findPreference(key) ?: return

        DataHolders.updateValue(requireActivity())

        DataHolders.printState()

        if (preference is TimePreference) {


            setAlarm(preference.hour, preference.minute)


        } else if (preference is SwitchPreference) {
            if (key == context?.getString(R.string.alarmEnabledKey)) {
                if (!DataHolders.alarmEnabled) {
                    cancelAlarm()
                } else {
                    val hour = DataHolders.alarmTime.split(":")[0].toInt()
                    val min = DataHolders.alarmTime.split(":")[1].toInt()
                    setAlarm(hour, min)
                }
            }
        } else if (preference is EditTextPreference) {

        }

    }

    fun setAlarm(hour: Int, min: Int) {
        if (!DataHolders.alarmEnabled) {
            return
        }
        if (DataHolders.alarmPending == AlarmState.PENDING) {
            cancelAlarm()
        }

        val alarmIntent = Intent(context, MyReceiver::class.java).let { intent ->
            intent.action = "ALARM_ACTION"
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
//            var today = true
//            Timber.i("${get(Calendar.HOUR_OF_DAY)} ${get(Calendar.HOUR)}")
//            if (get(Calendar.HOUR_OF_DAY) > hour)
//                today = false
//            if (get(Calendar.HOUR_OF_DAY) == hour && get(Calendar.MINUTE) >= min)
//                today = false
//            if (!today) {
//                add(Calendar.DAY_OF_MONTH, 1)
//            }
//            set(Calendar.HOUR_OF_DAY, hour)
//            set(Calendar.MINUTE, min)
//            set(Calendar.SECOND, 0)
            add(Calendar.SECOND, 10)
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
        DataHolders.setIsAlarmPending(requireContext(), AlarmState.PENDING)
        Toast.makeText(
            context,
            "Alarm Set For ${SimpleDateFormat.getDateTimeInstance().format(calendar.time)}",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cancelAlarm() {
        if (DataHolders.alarmPending != AlarmState.PENDING) {
            return
        }
        val alarmIntent = Intent(context, MyReceiver::class.java).let { intent ->
            intent.action = "ALARM_ACTION"
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        if (alarmIntent != null) {
            alarmManager.cancel(alarmIntent)
            Timber.i("Canceled")
        }
        DataHolders.setIsAlarmPending(requireContext(), AlarmState.NOT_SET)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }


    override fun onDisplayPreferenceDialog(preference: Preference) {
        var dialogFragment: DialogFragment? = null
        if (preference is TimePreference) {
            dialogFragment = TimePreferenceDialogFragment()
            val bundle = Bundle(1)
            bundle.putString("key", preference.getKey())
            dialogFragment.setArguments(bundle)
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(
                activity?.supportFragmentManager!!,
                "android.support.v7.preference.PreferenceFragment.DIALOG"
            )
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

}