package com.example.transxactshun.bills

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.transxactshun.MainActivity
import com.example.transxactshun.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class AddBillReminderActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    // view model
    private lateinit var addReminderViewModel: AddReminderViewModel

    // Set current time to calendar
    private val calendar = Calendar.getInstance()

    // UI Elements
    private lateinit var editTextPayeeName: EditText
    private lateinit var editTextAmount: EditText
    private lateinit var editTextMemo: EditText

    // Store user UID
    private lateinit var userUID: String

    // firebase db var
    private lateinit var database: DatabaseReference

    private lateinit var appContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bill_reminder)

        // init UI elements
        editTextPayeeName = findViewById(R.id.editTextPayeeName)
        editTextAmount = findViewById(R.id.editTextAmount)
        editTextMemo = findViewById(R.id.editTextMemo)

        // retrieve current user email
        val user = Firebase.auth.currentUser
        if (user != null) {
            userUID = user.uid
        } else {
            returnToLoginScreen()
        }

        // Initialize Firebase DB
        database = Firebase.database.reference

        appContext = this.applicationContext

        //init view model
        addReminderViewModel = ViewModelProvider(this).get(AddReminderViewModel::class.java)

        if (savedInstanceState == null) {
            // assign current dateTime to view model
            addReminderViewModel.remindOn = calendar
        }

    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
    }

    // Referenced DatePicker, TimePicker and Listener from Lecture 4 Layout Kotlin File
    // Link: https://www.sfu.ca/~xingdong/Teaching/CMPT362/code/Kotlin_code/LayoutKotlin.zip
    fun onClickDate(view: View) {
        val datePickerDialog = DatePickerDialog(
            this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun onClickTime(view: View) {
        val timePickerDialog = TimePickerDialog(
            this, this,
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        )
        timePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        addReminderViewModel.remindOn?.set(Calendar.YEAR, year)
        addReminderViewModel.remindOn?.set(Calendar.MONTH, monthOfYear)
        addReminderViewModel.remindOn?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        addReminderViewModel.remindOn?.set(Calendar.HOUR_OF_DAY, hourOfDay)
        addReminderViewModel.remindOn?.set(Calendar.MINUTE, minute)
    }

    fun onClickCancel(view: View) {
        this.finish()
    }

    fun onClickSave(view: View) {

        // retrieve values from UI elements
        addReminderViewModel.payeeName = editTextPayeeName.text.toString()

        if (!editTextAmount.text.isNullOrBlank()) {
            addReminderViewModel.amount = editTextAmount.text.toString().toDouble()
        }

        addReminderViewModel.memo = editTextMemo.text.toString()


        // create billEntry object
        val billEntry = BillEntry()
        billEntry.payeeName = addReminderViewModel.payeeName
        billEntry.amount = addReminderViewModel.amount
        billEntry.reminderDate = addReminderViewModel.remindOn?.timeInMillis
        billEntry.memo = addReminderViewModel.memo

        // generate random UID to store reminder
        var randomUID = UUID.randomUUID().toString()

        try {
            // insert entry to database using view model
            database.child("userReminder").child(userUID).child(randomUID).setValue(billEntry)

            Toast.makeText(
                this,
                "Saved!",
                Toast.LENGTH_SHORT
            )
                .show()
        } catch (ex: Exception) {
            Toast.makeText(
                this,
                "Unable to save data!",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        try {
            createReminderUsingAlarm()
        } catch (ex: Exception) {
            println("debug: $ex")
            Toast.makeText(
                this,
                "Unable to create notification alert!",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        // close activity
        this.finish()
    }

    private fun createReminderUsingAlarm() {
        val alarmIntent = Intent(appContext, BillReminderService::class.java)
        val alarmManager: AlarmManager = appContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            PendingIntent.getService(
                appContext,
                0,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            addReminderViewModel.remindOn!!.timeInMillis,
            pendingIntent
        )
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