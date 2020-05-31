package com.saleh.hw2_mobile99

import android.content.Context
import android.content.res.TypedArray
import android.icu.util.Calendar
import android.util.AttributeSet
import androidx.preference.DialogPreference


class TimePreference(context: Context?, attrs: AttributeSet?) : DialogPreference(context, attrs) {

    var hour = 0
    var minute = 0

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getString(index)!!
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val value: String = if (restoreValue) {
            if (defaultValue == null) {
                getPersistedString("00:00")
            } else {
                getPersistedString(defaultValue.toString())
            }
        } else {
            defaultValue.toString()
        }

        hour = parseHour(value)
        minute = parseMinute(value)
    }

    override fun getSummary(): CharSequence {
        return timeToString(hour, minute)
    }

    fun persistStringValue(value: String?) {
        persistString(value)
        summary = this.summary
    }

    companion object {
        fun parseHour(value: String): Int {
            return try {
                val time = value.split(":".toRegex()).toTypedArray()
                time[0].toInt()
            } catch (e: Exception) {
                0
            }
        }

        fun parseMinute(value: String): Int {
            return try {
                val time = value.split(":".toRegex()).toTypedArray()
                time[1].toInt()
            } catch (e: Exception) {
                0
            }
        }

        fun timeToString(h: Int, m: Int): String {
            return String.format("%02d", h) + ":" + String.format("%02d", m)
        }
    }
}