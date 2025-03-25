package com.example.mycontact.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun makeCall(context: Context, phoneNumber: String) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            (context as Activity),
            arrayOf(Manifest.permission.CALL_PHONE),
            1
        )
    } else {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(callIntent)
    }
}

fun isValidPhoneNumber(phone: String): Boolean {
    val cleaned = phone.filter { it.isDigit() }
    return cleaned.length in 9..12
}


