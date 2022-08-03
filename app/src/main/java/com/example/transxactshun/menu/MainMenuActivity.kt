package com.example.transxactshun.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.transxactshun.MainActivity
import com.example.transxactshun.R
import com.example.transxactshun.bills.BillReminderActivity
import com.example.transxactshun.bills.BudgetActivity
import com.example.transxactshun.transactions.TransactionHistoryActivity
import com.example.transxactshun.visualization.VisualizationActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainMenuActivity : AppCompatActivity() {

    // store current user email
    private lateinit var userEmail: String
    private lateinit var userEmailTextView: TextView

    // vars for gridView
    private lateinit var menuGridView: GridView
    private lateinit var menuAdapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // retrieve text view from UI
        userEmailTextView = findViewById(R.id.textViewUserEmail)
        // retrieve current user email
        val user = Firebase.auth.currentUser
        if (user != null) {
            userEmail = user.email.toString()

            // update text view
            userEmailTextView.text = "Email: $userEmail"
        } else {
            returnToLoginScreen()
        }

        // init view model
        val menuViewModel =
            ViewModelProvider(this).get(MenuViewModel::class.java)

        // menu options
        menuGridView = findViewById(R.id.menuGridView)
        menuAdapter = MenuAdapter(this, menuViewModel.menuItemArray, menuViewModel.imageIconArray)
        menuGridView.adapter = menuAdapter

        // menu click listener
        menuGridView.setOnItemClickListener { adapterView, view, position, id ->
            when (position) {
                0 -> openTransactionActivity()
                1 -> openGraphActivity()
                2 -> openBillReminderActivity()
                3 -> openBudgetActivity()
                4 -> openShareReceiptsActivity()
            }
        }

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

    fun onLogOutClick(view: View) {
        returnToLoginScreen()
    }

    private fun openTransactionActivity() {

        val intent = Intent(applicationContext, TransactionHistoryActivity::class.java).apply {
            // nothing for now
        }
        startActivity(intent)

    }

    private fun openGraphActivity() {
        val intent = Intent(applicationContext, VisualizationActivity::class.java).apply {
            // nothing for now
        }
        startActivity(intent)
    }

    private fun openBillReminderActivity() {
        val intent = Intent(applicationContext, BillReminderActivity::class.java)
        startActivity(intent)
    }

    private fun openBudgetActivity() {
        val intent = Intent(applicationContext, BudgetActivity::class.java)
        startActivity(intent)
    }

    private fun openShareReceiptsActivity() {

    }
}