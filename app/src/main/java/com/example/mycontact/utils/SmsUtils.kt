package com.example.mycontact.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun sendBulkSms(context: Context, phoneNumbers: List<String>, message: String) {
    if (phoneNumbers.isEmpty()) return

    val uri = Uri.parse("smsto:" + phoneNumbers.joinToString(";")) // Dùng ; hoặc , tùy máy
    val intent = Intent(Intent.ACTION_SENDTO, uri)
    intent.putExtra("sms_body", message)
    context.startActivity(intent)
}