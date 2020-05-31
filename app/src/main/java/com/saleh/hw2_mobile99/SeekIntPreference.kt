package com.saleh.hw2_mobile99

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceViewHolder
import androidx.preference.SeekBarPreference
import timber.log.Timber

class SeekIntPreference : SeekBarPreference {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun getSummary(): CharSequence {
        return (super.getSummary().toString() + value.toString())
    }

}