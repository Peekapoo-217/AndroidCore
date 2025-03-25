import com.example.mycontact.data.UserDAO
import com.example.mycontact.entities.Contact
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDAO: UserDAO) {

    // Lấy toàn bộ contact
    val allUsers: Flow<List<Contact>> = userDAO.getAllUsers()

    // Thêm contact
    suspend fun insert(contact: Contact) {
        userDAO.insert(contact)
    }

    suspend fun update(contact: Contact) {
        userDAO.update(contact)
    }
    // Xóa contact
    suspend fun delete(contact: Contact) {
        userDAO.delete(contact)
    }
}
