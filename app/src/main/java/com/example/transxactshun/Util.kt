package com.example.transxactshun

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Util {
    // Util library referenced from Lecture 2 CameraDemoKotlin app
    // Link: https://www.sfu.ca/~xingdong/Teaching/CMPT362/code/Kotlin_code/CameraDemoKotlin.zip
    // some modifications were made to include Read permission
    fun checkPermissions(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.SEND_SMS
                ),
                0
            )
        }
    }
}