package com.example.transxactshun.bills

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.GridView
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.example.transxactshun.R
import com.example.transxactshun.menu.MenuAdapter
import com.example.transxactshun.menu.MenuViewModel

class BillReminderActivity : AppCompatActivity() {

    // vars for reminder list
    private lateinit var reminderListView: ListView
    private lateinit var billListAdapter: BillListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_reminder)

        // init view model
        val billViewModel =
            ViewModelProvider(this).get(BillViewModel::class.java)

        // TODO: add entries to the listView only one time
        if (savedInstanceState == null) {
            var billaa = BillEntry("abc")
            billViewModel.reminderList.add(billaa)
        }

        // list options
        reminderListView = findViewById(R.id.listViewReminder)
        billListAdapter = BillListAdapter(this, billViewModel.reminderList)
        reminderListView.adapter = billListAdapter


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
}