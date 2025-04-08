/*
import com.example.mycontact.data.PhoneNumberDAO
import com.example.mycontact.data.UserDAO
import com.example.mycontact.entities.Contact
import com.example.mycontact.entities.ContactWithPhones
import com.example.mycontact.entities.PhoneNumber
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDAO: UserDAO, private val phoneNumberDAO: PhoneNumberDAO) {


    val allUsers: Flow<List<ContactWithPhones>> = userDAO.getAllUsers()


    suspend fun insert(contact: Contact, phones: List<String>) {

        userDAO.insert(listOf(contact))


        val insertedContact = userDAO.getAllRawContacts().find { it.name == contact.name }
            ?: return


        val phoneEntities = phones.map { number ->
            PhoneNumber(contactId = insertedContact.id, number = number)
        }
        phoneNumberDAO.insert(phoneEntities)
    }


    */
/*    suspend fun update(contact: Contact, phones: List<String>) {
            userDAO.update(contact)

            phoneNumberDAO.deleteAllPhoneNumbers()

            val phoneEntities = phones.map { number ->
                PhoneNumber(contactId = contact.id, number = number)
            }
            phoneNumberDAO.insert(phoneEntities)
        }*//*



    suspend fun delete(contact: Contact) {
        userDAO.delete(contact)
    }

    suspend fun isPhoneNumberExists(phone: String): Boolean {
        return phoneNumberDAO.findPhoneNumber(phone) != null
    }
}
*/
package com.example.mycontact.repositories

import android.content.Context
import com.example.mycontact.data.PhoneNumberDAO
import com.example.mycontact.data.UserDAO
import com.example.mycontact.entities.Contact
import com.example.mycontact.entities.ContactWithPhones
import com.example.mycontact.entities.PhoneNumber
import com.example.mycontact.utils.updateSystemContact
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDAO: UserDAO,
    private val phoneNumberDAO: PhoneNumberDAO,
    private val context: Context // 💡 để đồng bộ ra ngoài hệ thống
) {
    val allUsers: Flow<List<ContactWithPhones>> = userDAO.getAllUsers()

    suspend fun insert(contact: Contact, phones: List<String>) {
        // Insert contact vào Room
        val newId = userDAO.insertOne(contact) // ⚠ cần tạo hàm insertOne() trả về id

        val phoneEntities = phones.map { number ->
            PhoneNumber(contactId = newId.toInt(), number = number)
        }
        phoneNumberDAO.insert(phoneEntities)
    }

    suspend fun update(contact: Contact, phones: List<String>) {
        userDAO.update(contact)
        phoneNumberDAO.deleteByContactId(contact.id)

        val phoneEntities = phones.map { number ->
            PhoneNumber(contactId = contact.id, number = number)
        }
        phoneNumberDAO.insert(phoneEntities)

        // Đồng bộ ra danh bạ hệ thống nếu contact có nguồn gốc từ đó
        if (!contact.systemContactId.isNullOrBlank()) {
            updateSystemContact(context, contact, phones)
        }
    }

    suspend fun delete(contact: Contact) {
        userDAO.delete(contact)
    }

    suspend fun isPhoneNumberExists(phone: String): Boolean {
        return phoneNumberDAO.findPhoneNumber(phone) != null
    }
}

