package com.myapp.contactsmanager


import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class ContactRepository(private val contactDao: ContactDao) {
    val allContacts: LiveData<List<Contact>> = contactDao.getAllContacts()

    suspend fun insert(contact: Contact) {
        contactDao.insert(contact)
    }

    suspend fun findByPhoneNumber(phoneNumber: String): Contact? {
        return contactDao.findByPhoneNumber(phoneNumber)
    }
}
