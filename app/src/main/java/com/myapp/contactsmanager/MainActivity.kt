package com.myapp.contactsmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.myapp.contactsmanager.ContactAdapter
import com.myapp.contactsmanager.ContactViewModel
import com.myapp.contactsmanager.R


class MainActivity : AppCompatActivity() {
    private lateinit var contactViewModel: ContactViewModel
    private lateinit var adapter: ContactAdapter
    private val REQUEST_READ_CONTACTS = 67676

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView            =   findViewById<RecyclerView>(R.id.recyclerView)
        adapter                     =   ContactAdapter()
        recyclerView.adapter        =   adapter
        val layoutManager           = LinearLayoutManager(this) // or any other LayoutManager you are using
        recyclerView.layoutManager  = layoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var visibleItemPosition:Int = 0
            var  totalItemCount:Int     = 0

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // RecyclerView is not scrolling

                        val layoutInflater  = layoutInflater
                        val customToastView = layoutInflater.inflate(R.layout.custom_toast, null)
                        val textView:TextView       = customToastView.findViewById(R.id.toast_text_view)
                        val text:String             =  "Contact: " +visibleItemPosition + " Of " +totalItemCount
                        textView.text               = text

                        val toast = Toast(applicationContext)
                        toast.view = customToastView
                        toast.duration = Toast.LENGTH_SHORT
                        toast.show()
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        // RecyclerView is currently being scrolled by the user
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        // RecyclerView is settling after a fling or scroll
                    }
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Get the position of the first completely visible item
                visibleItemPosition =  layoutManager.findFirstCompletelyVisibleItemPosition() + 1 // To make it 1-based index

                totalItemCount      = adapter.itemCount


            }
        })

        // Set custom drawable resources
        recyclerView.setVerticalScrollbarThumbDrawable(ContextCompat.getDrawable(this, R.drawable.scrollbar_thumb))
        recyclerView.setVerticalScrollbarTrackDrawable(ContextCompat.getDrawable(this, R.drawable.scrollbar_track))

        contactViewModel            =   ViewModelProvider(this).get(ContactViewModel::class.java)
        contactViewModel.allContacts.observe(this, Observer { contacts ->
            contacts?.let { adapter.setContacts(it) }
        })

        contactViewModel.filteredContacts.observe(this, Observer { contacts ->
            contacts?.let { adapter.setContacts(it) }
        })


        val searchView = findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    newText?.let { query ->
                        contactViewModel.filterContacts(query)
                    }
                }
                return true
            }
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
