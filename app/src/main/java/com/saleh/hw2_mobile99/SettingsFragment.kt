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
import com.saleh.hw2_mobile99.alarmManager.AlarmManaging
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

        AlarmManaging.initMe(alarmManager)

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

            AlarmManaging.setAlarm("ALARM_ACTION", requireContext(), preference.hour, preference.minute)

        } else if (preference is SwitchPreference) {
            if (key == context?.getString(R.string.alarmEnabledKey)) {
                if (!DataHolders.alarmEnabled) {
                    AlarmManaging.cancelAlarm("ALARM_ACTION", requireContext())
                } else {
                    val hour = DataHolders.alarmTime.split(":")[0].toInt()
                    val min = DataHolders.alarmTime.split(":")[1].toInt()
                    AlarmManaging.setAlarm("ALARM_ACTION", requireContext(), hour, min)
                }
            }
        } else if (preference is EditTextPreference) {

        }

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