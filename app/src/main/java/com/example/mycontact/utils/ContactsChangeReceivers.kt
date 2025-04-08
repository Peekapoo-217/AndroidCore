package com.example.mycontact.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.mycontact.utils.helpers.ContactSyncHelper

class ContactsChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (ContactsContract.AUTHORITY == intent?.data?.authority) {
            Log.d("ContactsChangeReceiver", "Contacts changed!")
            // Gọi lại hàm đồng bộ từ danh bạ hệ thống vào Room
            CoroutineScope(Dispatchers.IO).launch {
                ContactSyncHelper.updateAllFromSystem(context)
            }
        }
    }
}
