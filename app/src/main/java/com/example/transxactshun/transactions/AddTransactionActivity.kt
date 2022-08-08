package com.example.transxactshun.transactions

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.transxactshun.MainActivity
import com.example.transxactshun.R
import com.example.transxactshun.bills.AddReminderViewModel
import com.example.transxactshun.database.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import java.util.*

class AddTransactionActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var saveNewTransactionBtn: Button
    private lateinit var cancelNewTransactionBtn: Button
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var purchaseNotesEditText: EditText
    private lateinit var vendorNameEditText: EditText

    // UI spinner elements
    private lateinit var spinnerPaymentType: Spinner
    private lateinit var spinnerCategoryType: Spinner

    // view model
    private lateinit var addTransactionViewModel: AddTransactionViewModel

    // Set current time to calendar
    private val calendar = Calendar.getInstance()


    // Store user UID
    private lateinit var userUID: String
    private lateinit var userEmail: String
    private lateinit var userPhone: String

    // firebase db var
    private lateinit var databaseFirebase: DatabaseReference


    // db
    private lateinit var database: ExpensesDatabase
    private lateinit var databaseDao: ExpensesDatabaseDao
    private lateinit var repository: ExpensesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Setup buttons
        saveNewTransactionBtn = findViewById(R.id.save_new_transaction)
        cancelNewTransactionBtn = findViewById(R.id.cancel_new_transaction)

        // retrieve current user email
        val user = Firebase.auth.currentUser
        if (user != null) {
            userUID = user.uid
            userEmail = user.email.toString()
        } else {
            returnToLoginScreen()
        }

        //init view model
        addTransactionViewModel = ViewModelProvider(this).get(AddTransactionViewModel::class.java)

        if (savedInstanceState == null) {
            // assign current dateTime to view model
            addTransactionViewModel.dateTransaction = calendar
        }

        // Setup EditText fields
        itemNameEditText = findViewById(R.id.addItemName)
        itemPriceEditText = findViewById(R.id.addItemPrice)
        vendorNameEditText = findViewById(R.id.addVendorName)
        purchaseNotesEditText = findViewById(R.id.addPurchaseNotes)
        spinnerPaymentType = findViewById(R.id.spinnerPaymentMethod)
        spinnerCategoryType = findViewById(R.id.spinnerItemCategoryMethod)


        // add data to spinner
        spinnerPaymentType.adapter = ArrayAdapter<PaymentType>(
            this,
            android.R.layout.simple_list_item_1,
            PaymentType.values()
        )

        spinnerCategoryType.adapter = ArrayAdapter<ExpenseCategory>(
            this,
            android.R.layout.simple_list_item_1,
            ExpenseCategory.values()
        )


        // Set up database
        database = ExpensesDatabase.getInstance(this)
        databaseDao = database.expensesDatabaseDao
        repository = ExpensesRepository(databaseDao)



        saveNewTransactionBtn.setOnClickListener {

            try {
                // Prepare data
                var itemName = itemNameEditText.text.toString()
                var vendorName = vendorNameEditText.text.toString()
                var price = 0
                val priceString = itemPriceEditText.text.toString()
                if (!priceString.isNullOrBlank()) {
                    price = priceString.toInt()
                }
                val paymentMethod = spinnerPaymentType.selectedItemPosition
                val itemCategories = spinnerCategoryType.selectedItemPosition
                val notes = purchaseNotesEditText.text.toString()
                val theDate = addTransactionViewModel.dateTransaction?.timeInMillis
                    ?: Calendar.getInstance().timeInMillis


                // Creating the expense data object
                val expensesObj = ExpensesDatabaseEntry(
                    email = userEmail,
                    category = itemCategories,
                    note = notes,
                    paymentType = paymentMethod,
                    epochDate = theDate,
                    cost = price,
                    vendor = vendorName,
                    items = itemName
                )

                println("debug: $expensesObj")

                // insert data object into the database
                repository.insert(expensesObj)

                Toast.makeText(
                    this,
                    "Saved!",
                    Toast.LENGTH_SHORT
                )
                    .show()

                calculateBudget()

            } catch (ex: Exception) {
                Toast.makeText(
                    this,
                    "Unable to save data!\n Please check data format!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            this.finish()
        }

        cancelNewTransactionBtn.setOnClickListener {
            this.finish()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
    }

    private fun calculateBudget() {
        try {


        } catch (ex: Exception) {
            Toast.makeText(
                baseContext,
                "Unable to calculate budget!",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun sendSmsNotification() {
        try {

        } catch (ex: Exception) {
            Toast.makeText(
                baseContext,
                "Unable to generate text notification, maybe your API account has lower balance!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun onClickDateTransaction(view: View) {
        val datePickerDialog = DatePickerDialog(
            this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        addTransactionViewModel.dateTransaction?.set(Calendar.YEAR, year)
        addTransactionViewModel.dateTransaction?.set(Calendar.MONTH, monthOfYear)
        addTransactionViewModel.dateTransaction?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
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