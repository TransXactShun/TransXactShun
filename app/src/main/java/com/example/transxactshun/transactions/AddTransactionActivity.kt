package com.example.transxactshun.transactions

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.transxactshun.R
import com.example.transxactshun.database.ExpensesDatabase
import com.example.transxactshun.database.ExpensesDatabaseDao
import com.example.transxactshun.database.ExpensesDatabaseEntry
import com.example.transxactshun.database.ExpensesRepository

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var saveNewTransactionBtn: Button
    private lateinit var cancelNewTransactionBtn: Button
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var paymentMethodEditText: EditText
    private lateinit var itemCategoryEditText: EditText
//    private lateinit var purchaseLocationEditText: EditText
    private lateinit var purchaseNotesEditText: EditText
    private lateinit var purchaseDateEditText: EditText

    private lateinit var database: ExpensesDatabase
    private lateinit var databaseDao: ExpensesDatabaseDao
    private lateinit var repository: ExpensesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Setup buttons
        saveNewTransactionBtn = findViewById(R.id.save_new_transaction)
        cancelNewTransactionBtn = findViewById(R.id.cancel_new_transaction)

        // Setup EditText fields
        itemNameEditText = findViewById(R.id.addItemName)
        itemPriceEditText = findViewById(R.id.addItemPrice)
        paymentMethodEditText = findViewById(R.id.addPaymentMethod)
        itemCategoryEditText = findViewById(R.id.addItemCategory)
        purchaseNotesEditText = findViewById(R.id.addPurchaseNotes)
        purchaseDateEditText = findViewById(R.id.addPurchaseDate)

        // Set up database
        database = ExpensesDatabase.getInstance(this)
        databaseDao = database.expensesDatabaseDao
        repository = ExpensesRepository(databaseDao)

        // Prepare data
        val price = itemPriceEditText.text.toString().toInt()
        val paymentMethod = paymentMethodEditText.text.toString().toInt()
        val itemCategories = itemCategoryEditText.text.toString().toInt()
        val notes = purchaseNotesEditText.text.toString()
        val theDate = purchaseDateEditText.text.toString().toLong()

        saveNewTransactionBtn.setOnClickListener {
            // Creating the expense data object
            val expensesObj = ExpensesDatabaseEntry(email = "test@abc.com",
                                                    category = itemCategories,
                                                    note = notes,
                                                    paymentType = paymentMethod,
                                                    vendor = "testing",
                                                    items = "test".toByteArray())
            expensesObj.cost = price
            expensesObj.epochDate = theDate

            // insert data object into the database
            repository.insert(expensesObj)

            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()

            finish()
        }

        cancelNewTransactionBtn.setOnClickListener {
            finish()
        }
    }

//    fun onSaveNewTransaction(): Int {
//        // TODO: Not yet implemented
//        println("TRACE: Save clicked")
//
//        return 0
//    }
//
//    fun onCancelNewTransaction() {
//        finish()
//    }
}