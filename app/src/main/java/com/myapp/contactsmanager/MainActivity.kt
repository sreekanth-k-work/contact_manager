package com.myapp.contactsmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapp.contactsmanager.ContactAdapter
import com.myapp.contactsmanager.ContactViewModel
import com.myapp.contactsmanager.R


class MainActivity : AppCompatActivity() {
    private lateinit var contactViewModel: ContactViewModel
    private lateinit var adapter: ContactAdapter
    private val REQUEST_READ_CONTACTS = 67676

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView            =   findViewById<RecyclerView>(R.id.recyclerView)
        adapter                     =   ContactAdapter()
        recyclerView.adapter        =   adapter
        recyclerView.layoutManager  =   LinearLayoutManager(this)

        contactViewModel            =   ViewModelProvider(this).get(ContactViewModel::class.java)
        contactViewModel.allContacts.observe(this, Observer { contacts ->
            contacts?.let { adapter.setContacts(it) }
        })

        requestContactsPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchContactsFromPhoneBook()
            }
        }
    }


    private fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS)
        } else {
            fetchContactsFromPhoneBook()
        }
    }



    private fun fetchContactsFromPhoneBook() {
        val contacts = mutableListOf<Contact>()

        val contentResolver = contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phoneNumber = it.getString(phoneNumberIndex)
                contacts.add(Contact(name = name, phoneNumber = phoneNumber))
            }
        }

        contacts.forEach { contactViewModel.insert(it) }
    }
}
