package com.myapp.contactsmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope



class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ContactRepository
    val allContacts: LiveData<List<Contact>>
    private val _filteredContacts = MutableLiveData<List<Contact>>()
    val filteredContacts: LiveData<List<Contact>> = _filteredContacts

    init {
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        repository = ContactRepository(contactDao)
        allContacts = repository.allContacts
    }

    fun insert(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }


    fun filterContacts(query: String) = viewModelScope.launch {
        val lowercaseQuery = query.toLowerCase()
        _filteredContacts.value = allContacts.value?.filter { contact ->
            contact.name.toLowerCase().contains(lowercaseQuery) ||
                    contact.phoneNumber.contains(lowercaseQuery)
        }
    }
}


