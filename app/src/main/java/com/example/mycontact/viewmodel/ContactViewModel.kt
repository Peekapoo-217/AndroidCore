package com.example.mycontact.viewmodel

import com.example.mycontact.data.ContactDatabase
import UserRepository

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mycontact.entities.Contact
import com.example.mycontact.entities.ContactWithPhones
import com.example.mycontact.utils.deleteContactFromSystem
import com.example.mycontact.utils.readSystemContacts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    // Repository
    private val repository: UserRepository

    // Expose LiveData to UI
    val allContacts: LiveData<List<ContactWithPhones>>

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        val contactDao = ContactDatabase.getDatabase(application).contactDAO()
        val phoneDao = ContactDatabase.getDatabase(application).phoneNumberDAO()
        repository = UserRepository(contactDao, phoneDao)
        allContacts = repository.allUsers.asLiveData()
    }

    // Insert contact
    fun insertContact(contact: Contact, phone: List<String>) = viewModelScope.launch {
        repository.insert(contact, phone)
    }

    fun updateContactWithPhones(contact: Contact, phones: List<String>) = viewModelScope.launch {

        repository.update(contact, phones)
    }

    // Update search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredContacts(list: List<ContactWithPhones>): List<ContactWithPhones> {
        return if (_searchQuery.value.isBlank()) {
            list
        } else {
            list.filter { contactWithPhones ->
                contactWithPhones.contact.name.contains(_searchQuery.value, ignoreCase = true) ||
                        contactWithPhones.phone.any {
                            it.number.contains(
                                _searchQuery.value,
                                ignoreCase = true
                            )
                        }
            }
        }
    }

    fun importSystemContacts(context: Context) = viewModelScope.launch {
        val contacts = readSystemContacts(context)

        contacts
            .groupBy({ it.first }, { it.second })
            .forEach { (name, phones) ->
                val contact = Contact(name = name)
                repository.insert(contact, phones)
            }
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        repository.delete(contact)

        deleteContactFromSystem(
            getApplication<Application>().applicationContext,
            contact.id.toString()
        )
    }

    suspend fun isPhoneNumberExists(phone: String): Boolean {
        return repository.isPhoneNumberExists(phone)
    }

}

