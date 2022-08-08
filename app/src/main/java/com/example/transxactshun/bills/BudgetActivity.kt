package com.example.transxactshun.bills

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.transxactshun.MainActivity
import com.example.transxactshun.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

class BudgetActivity : AppCompatActivity() {

    private val DEF_BUDGET = 500.0

    // Store user UID
    private lateinit var userUID: String

    // firebase db var
    private lateinit var database: DatabaseReference

    // var for UI
    private lateinit var textViewDisplayBudget: TextView
    private lateinit var editTextUpdateBudget: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

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
        val budgetViewModel =
            ViewModelProvider(this).get(BudgetViewModel::class.java)

        // retrieve UI elements
        textViewDisplayBudget = findViewById(R.id.textViewCurrentBudgetAmount)
        editTextUpdateBudget = findViewById(R.id.editTextNewBudget)


        // retrieve values from firebase only once
        if (savedInstanceState == null) {

            database.child("userBudget").child(userUID).child("budget").get().addOnSuccessListener {
                if (it.value == null) {
                    budgetViewModel.currentBudgetAmount.value = DEF_BUDGET
                } else {
                    budgetViewModel.currentBudgetAmount.value = it.value.toString().toDouble()
                }

            }.addOnFailureListener {
                println("debug: Error getting data")
            }

        }

        budgetViewModel.currentBudgetAmount.observe(this, Observer {
            // display current budget
            textViewDisplayBudget.text = "$ ${it.toString()}"
        })


    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
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

    fun onClickBudgetBack(view: View) {
        this.finish()
    }

    fun onClickBudgetUpdate(view: View) {

        try {

            var budgetString = editTextUpdateBudget.text.toString()

            if (budgetString.isNullOrBlank()) {
                budgetString = "0.0"
            }

            // retrieve data from edit text field
            var updatedBudget: Double = budgetString.toDouble()

            // round to 2 decimal digits
            updatedBudget = (updatedBudget * 100.0).roundToInt() / 100.0

            // update data in firebase db
            database.child("userBudget").child(userUID).child("budget").setValue(updatedBudget)

        } catch (ex: Exception) {
            Toast.makeText(
                baseContext,
                "Please check your input or db connection might not be available!",
                Toast.LENGTH_SHORT
            ).show()

        }
        this.finish()
    }
}