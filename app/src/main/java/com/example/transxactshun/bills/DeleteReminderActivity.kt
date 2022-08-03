package com.example.transxactshun.bills

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.transxactshun.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class DeleteReminderActivity : AppCompatActivity() {

    // firebase db var
    private lateinit var database: DatabaseReference

    //UI Elements
    private lateinit var textViewId: TextView
    private lateinit var textViewPayee: TextView

    // store vars to delete entry from DB
    private lateinit var userId: String
    private lateinit var reminderId: String
    private lateinit var payeeName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_reminder)

        // init UI elements
        textViewId = findViewById(R.id.textViewReminderId)
        textViewPayee = findViewById(R.id.textViewReminderPayee)


        // Initialize Firebase DB
        database = Firebase.database.reference

        // retrieve bundled values
        userId = intent.getStringExtra("USER_UID").toString()
        reminderId = intent.getStringExtra("REMINDER_UID").toString()
        payeeName = intent.getStringExtra("PAYEE").toString()

        // display data on screen
        textViewId.text = "Reminder ID: $reminderId"
        textViewPayee.text = "Payee Name: $payeeName"

    }

    fun onClickDelete(view: View) {
        // remove entry from firebase db
        try {
            database.child("userReminder").child(userId).child(reminderId).removeValue()

            Toast.makeText(
                baseContext,
                "Reminder Deleted!",
                Toast.LENGTH_SHORT
            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                baseContext,
                "Unable to delete reminder!",
                Toast.LENGTH_SHORT
            ).show()
        }

        this.finish()
    }

    fun onClickCancelReminder(view: View) {
        this.finish()
    }
}