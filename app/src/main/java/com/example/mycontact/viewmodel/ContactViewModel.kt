package com.example.mycontact.viewmodel

import com.example.mycontact.data.ContactDatabase
import UserRepository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mycontact.entities.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    // Repository
    private val repository: UserRepository

    // Expose LiveData to UI
    val allContacts: LiveData<List<Contact>>

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        val contactDao = ContactDatabase.getDatabase(application).contactDAO()
        repository = UserRepository(contactDao)
        allContacts = repository.allUsers.asLiveData()
    }

    // Insert contact
    fun insertContact(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }

    fun updateContact(updatedContact: Contact) = viewModelScope.launch {
        repository.update(updatedContact)
    }

    // Delete contact
    fun deleteContact(contact: Contact) = viewModelScope.launch {
        repository.delete(contact)
    }

    // Update search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Filtered contacts (optional)
    fun getFilteredContacts(list: List<Contact>): List<Contact> {
        return if (_searchQuery.value.isBlank()) {
            list
        } else {
            list.filter { contact ->
                contact.name.contains(_searchQuery.value, ignoreCase = true) ||
                        contact.phoneNumber.any { it.contains(_searchQuery.value, ignoreCase = true) }
            }
        }
    }

    suspend fun isPhoneNumberExists(phone : String): Boolean{
        return repository.isPhoneNumberExists(phone)
    }


}