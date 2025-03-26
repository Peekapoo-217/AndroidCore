import com.example.mycontact.data.UserDAO
import com.example.mycontact.entities.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository(private val userDAO: UserDAO) {

    // Lấy toàn bộ contact
    val allUsers: Flow<List<Contact>> = userDAO.getAllUsers()

    suspend fun insert(contact: Contact) {
        userDAO.insert(contact)
    }

    suspend fun update(contact: Contact) {
        userDAO.update(contact)
    }

    suspend fun delete(contact: Contact) {
        userDAO.delete(contact)
    }

    suspend fun isPhoneNumberExists(phone: String): Boolean {
        val contactList = userDAO.getAllUsers().first()
        return contactList.any { contact ->
            contact.phoneNumber.any { it == phone }
        }
    }
}
