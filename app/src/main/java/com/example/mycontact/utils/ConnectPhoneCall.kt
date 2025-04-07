package com.example.mycontact.utils

import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
import android.util.Log

fun readSystemContacts(context: Context): List<Pair<String, String>> {
    val list = mutableListOf<Pair<String, String>>()
    val resolver = context.contentResolver

    val cursor = resolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null, null, null, null
    )

    cursor?.use {
        val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        while (it.moveToNext()) {
            val name = it.getString(nameIdx) ?: ""
            val number = it.getString(numberIdx) ?: ""
            list.add(name to number)
        }
    }

    return list
}

fun addContactToSystem(context: Context, name: String, phone: String) {
    val ops = ArrayList<ContentProviderOperation>()

    ops.add(
        ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            .build()
    )

    ops.add(
        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            .build()
    )

    ops.add(
        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            .build()
    )

    context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
}

fun deleteContactFromSystem(context: Context, contactId: String) {
    try {
        val ops = ArrayList<ContentProviderOperation>()

        ops.add(
            ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                )
                .build()
        )

        ops.add(
            ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection("${ContactsContract.RawContacts._ID} = ?", arrayOf(contactId))
                .build()
        )

        // Áp dụng tất cả các thao tác xóa
        context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)

        Log.d(
            "DeleteContact",
            "Đã xóa liên hệ và các số điện thoại liên quan với ID: $contactId"
        )
    } catch (e: Exception) {
        Log.e("DeleteContact", "Lỗi khi xóa liên hệ và số điện thoại", e)
    }
}




