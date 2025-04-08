package com.example.mycontact.utils.helpers

import android.content.Context
import com.example.mycontact.data.ContactDatabase
import com.example.mycontact.entities.Contact
import com.example.mycontact.entities.PhoneNumber
import com.example.mycontact.utils.readSystemContacts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContactSyncHelper {

    suspend fun updateAllFromSystem(context: Context) = withContext(Dispatchers.IO) {
        val rawList = readSystemContacts(context) // List<Pair<String, String>>

        // ✅ Gom lại Map<Tên, Set<SĐT>> để tránh trùng
        val groupedMap = mutableMapOf<String, MutableSet<String>>()

        for ((name, number) in rawList) {
            val cleanedName = name.trim()
            val cleanedNumber = number.trim()
            if (cleanedName.isNotBlank() && cleanedNumber.isNotBlank()) {
                groupedMap.getOrPut(cleanedName) { mutableSetOf() }.add(cleanedNumber)
            }
        }

        // ✅ Tạo danh sách Contact từ key
        val contactList = groupedMap.keys.map { Contact(name = it) }

        val db = ContactDatabase.getDatabase(context)
        db.phoneNumberDAO().deleteAllPhoneNumbers()
        db.contactDAO().deleteAllContacts()

        // ✅ Insert contact → Room sinh id
        db.contactDAO().insert(contactList)

        // ✅ Lấy lại danh sách đã insert
        val insertedContacts = db.contactDAO().getAllRawContacts()

        // ✅ Map số với đúng contactId
        val phoneNumbers = groupedMap.flatMap { (name, numbers) ->
            val contact = insertedContacts.find { it.name == name }
            contact?.let {
                numbers.distinct().map { number ->
                    PhoneNumber(number = number, contactId = it.id)
                }
            } ?: emptyList()
        }

        db.phoneNumberDAO().insert(phoneNumbers)

    }
}

