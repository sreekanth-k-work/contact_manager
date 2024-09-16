package com.myapp.contactsmanager;

import androidx.lifecycle.LiveData
import androidx.room.OnConflictStrategy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts")
    fun getAllContacts(): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Query("SELECT * FROM contacts WHERE phoneNumber LIKE :phoneNumber LIMIT 1")
    suspend fun findByPhoneNumber(phoneNumber: String): Contact?
}
