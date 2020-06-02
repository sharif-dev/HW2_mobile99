package com.saleh.hw2_mobile99

import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import timber.log.Timber


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate")
        setContentView(R.layout.settings_activity)
        supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment())
            .commit()
    }

    override fun onResume() {
        super.onResume()
        requestOverlayPermission()
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!Settings.canDrawOverlays(this)) {
                val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + this.packageName)
                            )
                            startActivityForResult(intent, 124)
                        }
                        else -> {

                        }
                    }
                }
                val builder = AlertDialog.Builder(this)
                builder.setMessage("For application work correctly in your phone you must grant APPEAR ON TOP permission.")
                    .setPositiveButton("OK", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 124) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Timber.i("Permision denied")
                } else {
                    Timber.i("Permision workecd")
                }
            }
        }
    }
}
