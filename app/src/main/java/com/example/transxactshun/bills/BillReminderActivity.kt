package com.example.transxactshun.bills

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.GridView
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.transxactshun.MainActivity
import com.example.transxactshun.R
import com.example.transxactshun.menu.MenuAdapter
import com.example.transxactshun.menu.MenuViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class BillReminderActivity : AppCompatActivity() {

    // vars for reminder list
    private lateinit var reminderListView: ListView
    private lateinit var billListAdapter: BillListAdapter
    private lateinit var billArrayList: ArrayList<BillEntry>

    // Store user UID
    private lateinit var userUID: String

    // firebase db var
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_reminder)

        // retrieve current user email
        val user = Firebase.auth.currentUser
        if (user != null) {
            userUID = user.uid
        } else {
            returnToLoginScreen()
        }

        // Initialize Firebase DB
        database = Firebase.database.reference

        // init view model
        val billViewModel =
            ViewModelProvider(this).get(BillViewModel::class.java)

        // list options
        reminderListView = findViewById(R.id.listViewReminder)
        billArrayList = ArrayList()
        billListAdapter = BillListAdapter(this, billArrayList)
        reminderListView.adapter = billListAdapter


        // add entries to the listView only one time
        if (savedInstanceState == null) {
            val remindersListQuery = database.child("userReminder").child(userUID)

            remindersListQuery.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    println("onChildAdded:" + snapshot.key!!)

                    // A new comment has been added, add it to the displayed list
                    val billEntryData = snapshot.getValue<BillEntry>()

                    // add entry to list
                    if (billEntryData != null) {

                        billViewModel.reminderListLiveData.value =
                            billViewModel.reminderListLiveData.value?.plus(billEntryData) ?: listOf(
                                billEntryData
                            )
                    }

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

        // add observer if any changes to the list
        billViewModel.reminderListLiveData.observe(this, Observer { it ->
            billListAdapter.replace(it)
            billListAdapter.notifyDataSetChanged()
        })


    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
    }


    fun onClickAddReminder(view: View) {
        val intent = Intent(this, AddBillReminderActivity::class.java)
        startActivity(intent)
    }

    fun onClickBack(view: View) {
        this.finish()
    }

    private fun returnToLoginScreen() {
        Toast.makeText(
            baseContext,
            "User logged out!",
            Toast.LENGTH_SHORT
        ).show()

        // return to main activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()

    }
}