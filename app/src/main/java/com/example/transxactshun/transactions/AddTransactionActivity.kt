package com.example.transxactshun.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.transxactshun.R
import com.example.transxactshun.bills.AddReminderViewModel
import com.example.transxactshun.database.ExpensesDatabase
import com.example.transxactshun.database.ExpensesDatabaseDao
import com.example.transxactshun.database.ExpensesDatabaseEntry
import com.example.transxactshun.database.ExpensesRepository
import java.util.*

class AddTransactionActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    enum class AddPaymentType(val string: String) {
        CASH("Cash"),
        DEBIT("Debit"),
        CREDIT("Credit"),
        CHEQUE("Cheque"),
        GIFT("Gift Card/Certificate")
    }

    enum class AddExpenseCategory(val string: String) {
        GROCERY("Grocery"),
        RESTAURANTS("Restaurants"),
        ENTERTAINMENT("Entertainment"),
        TRAVEL("Travel"),
        RECURRING("Recurring Bill Payments"),
        PERSONAL("Personal"),
        TECHNOLOGY("Technology"),
        TRANSPORTATION("Transportation"),
        MEDICAL("Medical"),
        HOME("Home"),
        EDUCATION("Education"),
        CONSTRUCTION("Construction"),
        OFFICE("Office"),
        FINANCIAL("Financial"),
        OTHER("Other")
    }

    private lateinit var saveNewTransactionBtn: Button
    private lateinit var cancelNewTransactionBtn: Button
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var purchaseNotesEditText: EditText

    // UI spinner elements
    private lateinit var spinnerPaymentType: Spinner
    private lateinit var spinnerCategoryType: Spinner

    // view model
    private lateinit var addTransactionViewModel: AddTransactionViewModel

    // Set current time to calendar
    private val calendar = Calendar.getInstance()

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

        //init view model
        addTransactionViewModel = ViewModelProvider(this).get(AddTransactionViewModel::class.java)

        if (savedInstanceState == null) {
            // assign current dateTime to view model
            addTransactionViewModel.dateTransaction = calendar
        }

        // Setup EditText fields
        itemNameEditText = findViewById(R.id.addItemName)
        itemPriceEditText = findViewById(R.id.addItemPrice)
        purchaseNotesEditText = findViewById(R.id.addPurchaseNotes)
        spinnerPaymentType = findViewById(R.id.spinnerPaymentMethod)
        spinnerCategoryType = findViewById(R.id.spinnerItemCategoryMethod)

        // add data to spinner
        spinnerPaymentType.adapter = ArrayAdapter<AddPaymentType>(
            this,
            android.R.layout.simple_list_item_1,
            AddPaymentType.values()
        )

        spinnerCategoryType.adapter = ArrayAdapter<AddExpenseCategory>(
            this,
            android.R.layout.simple_list_item_1,
            AddExpenseCategory.values()
        )


        // Set up database
        database = ExpensesDatabase.getInstance(this)
        databaseDao = database.expensesDatabaseDao
        repository = ExpensesRepository(databaseDao)



        saveNewTransactionBtn.setOnClickListener {

            try {
                // Prepare data
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
                    email = "test@abc.com",
                    category = itemCategories,
                    note = notes,
                    paymentType = paymentMethod,
                    epochDate = theDate,
                    cost = price,
                    vendor = "testing",
                    items = "test".toByteArray()
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

}