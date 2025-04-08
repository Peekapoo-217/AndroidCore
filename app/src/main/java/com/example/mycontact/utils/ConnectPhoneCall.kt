package com.example.mycontact.utils

import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.example.mycontact.data.ContactDatabase
import com.example.mycontact.entities.Contact
import com.example.mycontact.entities.PhoneNumber
import com.example.mycontact.entities.SystemContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun readSystemContacts(context: Context): List<SystemContact> {
    val resolver = context.contentResolver

    val contactsMap = mutableMapOf<String, Pair<String, MutableSet<String>>>()

    val cursor = resolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        ),
        null,
        null,
        null
    )

    cursor?.use {
        val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        while (it.moveToNext()) {
            val contactId = it.getString(idIndex) ?: continue
            val name = it.getString(nameIndex)?.trim() ?: "Không tên"
            val rawNumber = it.getString(numberIndex)?.trim() ?: continue

            val cleanedNumber = rawNumber.replace("[^\\d+]".toRegex(), "") // giữ số & +
            if (cleanedNumber.isBlank()) continue

            val entry = contactsMap.getOrPut(contactId) { name to mutableSetOf() }
            entry.second.add(cleanedNumber)
        }
    }

    return contactsMap.map { (id, pair) ->
        SystemContact(
            systemContactId = id,
            name = pair.first,
            numbers = pair.second.toList()
        )
    }
}

fun getRawContactId(context: Context, systemContactId: String): Long? {
    val cursor = context.contentResolver.query(
        ContactsContract.RawContacts.CONTENT_URI,
        arrayOf(ContactsContract.RawContacts._ID),
        "${ContactsContract.RawContacts.CONTACT_ID}=?",
        arrayOf(systemContactId),
        null
    )
    cursor?.use {
        if (it.moveToFirst()) {
            return it.getLong(0)
        }
    }
    return null
}

fun updateSystemContact(context: Context, contact: Contact, numbers: List<String>) {
    val contentResolver = context.contentResolver
    val rawContactId = getRawContactId(context, contact.systemContactId ?: return) ?: return

    // Xóa hết số cũ
    val ops = mutableListOf<ContentProviderOperation>()
    ops.add(
        ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
            .withSelection(
                "${ContactsContract.Data.RAW_CONTACT_ID}=? AND ${ContactsContract.Data.MIMETYPE}=?",
                arrayOf(
                    rawContactId.toString(),
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
            ).build()
    )

    // Thêm lại số mới
    for (number in numbers.distinct()) {
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                )
                .build()
        )
    }

    // (Tùy chọn) cập nhật tên nếu cần
    ops.add(
        ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
            .withSelection(
                "${ContactsContract.Data.RAW_CONTACT_ID}=? AND ${ContactsContract.Data.MIMETYPE}=?",
                arrayOf(
                    rawContactId.toString(),
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
            )
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.name)
            .build()
    )

    // Gửi đi
    if (ops.isNotEmpty()) {
        contentResolver.applyBatch(
            ContactsContract.AUTHORITY,
            ops as java.util.ArrayList<ContentProviderOperation>
        )
    }
}


suspend fun syncFromSystemContacts(context: Context) = withContext(Dispatchers.IO) {
    val systemContacts = readSystemContacts(context)
    val db = ContactDatabase.getDatabase(context)

    val userDao = db.contactDAO()  // hoặc userDAO nếu bạn đặt tên khác
    val phoneDao = db.phoneNumberDAO()

    val localContacts = userDao.getAllRawContacts()

    for (sysContact in systemContacts) {
        // Kiểm tra xem đã tồn tại contact có systemContactId đó chưa
        val existingContact =
            localContacts.find { it.systemContactId == sysContact.systemContactId }

        if (existingContact != null) {
            // Đã có → cập nhật số điện thoại
            phoneDao.deleteByContactId(existingContact.id)

            val phoneNumbers = sysContact.numbers.distinct().map {
                PhoneNumber(contactId = existingContact.id, number = it)
            }
            phoneDao.insert(phoneNumbers)
        } else {
            // Chưa có → thêm mới vào Room
            val newContact =
                Contact(name = sysContact.name, systemContactId = sysContact.systemContactId)
            val newId = userDao.insertOne(newContact) // ⚠ cần tạo hàm insert trả về id

            val phoneNumbers = sysContact.numbers.distinct().map {
                PhoneNumber(contactId = newId.toInt(), number = it)
            }
            phoneDao.insert(phoneNumbers)
        }
    }
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
            .withValue(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )
            .build()
    )

    context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
}

/*fun addContactToSystem(context: Context, name: String, phone: String) {
    val resolver = context.contentResolver

    // Kiểm tra xem contact đã tồn tại chưa
    val contactCursor = resolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        arrayOf(ContactsContract.Contacts._ID),
        "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} = ?",
        arrayOf(name),
        null
    )

    if (contactCursor != null && contactCursor.moveToFirst()) {
        // ✅ Contact đã tồn tại → thêm số vào contact đó
        contactCursor.close()
        addPhoneNumberToExistingContact(context, contactName = name, newPhone = phone)
    } else {
        contactCursor?.close()

        // Chưa có contact → tạo mới (giữ nguyên như cũ)
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
                .withValue(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                )
                .build()
        )

        resolver.applyBatch(ContactsContract.AUTHORITY, ops)
    }
}*/

/*fun addPhoneNumberToExistingContact(context: Context, contactName: String, newPhone: String) {
    val resolver = context.contentResolver

    // 1. Tìm contactId từ contactName
    val contactCursor = resolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        arrayOf(ContactsContract.Contacts._ID),
        "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} = ?",
        arrayOf(contactName),
        null
    )

    if (contactCursor != null && contactCursor.moveToFirst()) {
        val contactId = contactCursor.getString(0)

        // 2. Lấy đúng rawContactId (cần thiết để insert)
        val rawCursor = resolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            arrayOf(ContactsContract.RawContacts._ID),
            "${ContactsContract.RawContacts.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )

        if (rawCursor != null && rawCursor.moveToFirst()) {
            val rawContactId = rawCursor.getLong(0)

            val ops = ArrayList<ContentProviderOperation>()
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhone)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY, 0)
                    .withValue(ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY, 0)
                    .build()
            )

            resolver.applyBatch(ContactsContract.AUTHORITY, ops)
            rawCursor.close()
        }

        contactCursor.close()
    }
}*/


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




