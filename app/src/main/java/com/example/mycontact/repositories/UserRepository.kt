import com.example.mycontact.data.PhoneNumberDAO
import com.example.mycontact.data.UserDAO
import com.example.mycontact.entities.Contact
import com.example.mycontact.entities.ContactWithPhones
import com.example.mycontact.entities.PhoneNumber
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDAO: UserDAO, private val phoneNumberDAO: PhoneNumberDAO) {

    // Lấy toàn bộ contact
    val allUsers: Flow<List<ContactWithPhones>> = userDAO.getAllUsers()

    suspend fun insert(contact: Contact, phone: List<String>) {
        val contactId =
            userDAO.insert(contact)
        phone.forEach { number ->
            phoneNumberDAO.insert(PhoneNumber(contactId = contactId.toInt(), number = number))
        }
    }

    suspend fun update(contact: Contact, phones: List<String>) {
        userDAO.update(contact)

        phoneNumberDAO.deleteAllPhoneForContact(contact.id)

        phones.forEach {
            number -> phoneNumberDAO.insert(PhoneNumber(contactId = contact.id, number = number))
        }
    }

    suspend fun delete(contact: Contact) {
        userDAO.delete(contact)
    }

    suspend fun isPhoneNumberExists(phone: String): Boolean {
        return phoneNumberDAO.findPhoneNumber(phone) != null
    }
}
