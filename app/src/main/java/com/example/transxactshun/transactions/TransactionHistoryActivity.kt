package com.example.transxactshun.transactions

import android.content.ClipData.newIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.transxactshun.R
import com.example.transxactshun.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class TransactionHistoryActivity : AppCompatActivity() {
    companion object {
        const val TRANSACTION_ID_KEY = "id"
        const val TRANSACTION_POSITION_KEY = "position"
        const val TRANSACTION_NAME_KEY = "itemName"
        const val TRANSACTION_PRICE_KEY = "itemPrice"
        const val TRANSACTION_DATE_KEY = "purchaseDate"
        const val TRANSACTION_PAYMENT_KEY = "paymentMethod"
        const val TRANSACTION_NOTES_KEY = "purchaseNotes"
        const val TRANSACTION_CATEGORY_KEY = "itemCategory"
        const val TRANSACTION_EMAIL_KEY = "email"
        const val TRANSACTION_VENDOR_KEY = "vendor"
        const val TRANSACTION_LAT_KEY = "locationLat"
        const val TRANSACTION_LNG_KEY = "LocationLng"
    }
    private lateinit var transactionListView: ListView
    private lateinit var addTransactionButton: Button
    private lateinit var transactionSelected: ExpensesDatabaseEntry
    private lateinit var transactionHistoryDatabase: ExpensesDatabase
    private lateinit var transactionHistoryDao: ExpensesDatabaseDao
    private lateinit var transactionHistoryRepository: ExpensesRepository
    private lateinit var transactionHistoryViewModelFactory: TransactionHistoryViewModelFactory
    lateinit var transactionHistoryViewModel: TransactionHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)
        transactionListView = findViewById(R.id.transactionListView)

        addTransactionButton = findViewById(R.id.addNewTransactionButton)
        addTransactionButton.setOnClickListener {
            val createTransactionIntent = Intent(applicationContext, AddTransactionActivity::class.java).apply {
                // nothing for now
            }
            startActivity(createTransactionIntent)
        }

        CoroutineScope(IO).launch {
            transactionHistoryDatabase = ExpensesDatabase.getInstance(this@TransactionHistoryActivity)
            transactionHistoryDao = transactionHistoryDatabase.expensesDatabaseDao
            transactionHistoryRepository = ExpensesRepository(transactionHistoryDao)
            transactionHistoryViewModelFactory = TransactionHistoryViewModelFactory(transactionHistoryRepository)
            transactionHistoryViewModel =
                ViewModelProvider(
                    this@TransactionHistoryActivity,
                    transactionHistoryViewModelFactory
                )[TransactionHistoryViewModel::class.java]

            withContext(Main) {
                transactionHistoryViewModel.expensesHistory.observe(this@TransactionHistoryActivity) {
                    // Uses the TransactionAdapter class to create list items
                    val adapter = TransactionAdapter(this@TransactionHistoryActivity, it)
                    transactionListView.adapter = adapter
                    adapter.notifyDataSetChanged()

                    // Transactions are clickable by starting the EditTransactionActivity
                    transactionListView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                        // Get the selected Transaction
                        transactionSelected = it[position]

//                        // Send the values of the selected Transaction to the next activity
                        val editTransactionIntent = Intent(this@TransactionHistoryActivity, EditTransactionActivity::class.java)
                        editTransactionIntent.putExtra(TRANSACTION_ID_KEY, transactionSelected.id)
                        editTransactionIntent.putExtra(TRANSACTION_POSITION_KEY, position)
                        editTransactionIntent.putExtra(TRANSACTION_EMAIL_KEY, transactionSelected.email)
                        editTransactionIntent.putExtra(TRANSACTION_NAME_KEY, transactionSelected.items)
                        editTransactionIntent.putExtra(TRANSACTION_PRICE_KEY, transactionSelected.cost)
                        editTransactionIntent.putExtra(TRANSACTION_DATE_KEY, transactionSelected.epochDate)
                        editTransactionIntent.putExtra(TRANSACTION_PAYMENT_KEY, transactionSelected.paymentType)
                        editTransactionIntent.putExtra(TRANSACTION_NOTES_KEY, transactionSelected.note)
                        editTransactionIntent.putExtra(TRANSACTION_EMAIL_KEY, transactionSelected.email)
                        editTransactionIntent.putExtra(TRANSACTION_CATEGORY_KEY, transactionSelected.category)
                        startActivity(editTransactionIntent)
                    }
                }
            }
        }
    }

    // When EditTransactionActivity starts, the current activity is onPause() and onStop().
    // onResume() be called once it ends
    override fun onResume() {
        super.onResume()
        // If EditTransactionHistory was called and Save was used
        val editTransactionCalled = intent.getBooleanExtra("EditTransactionCalled", false) // Returns a Boolean value"
        if (editTransactionCalled) {
            println("TRACE: IF STATEMENT WAS HIT")
            // Save was called retrieve new values sent by putExtra()
            val id = intent.getLongExtra(TRANSACTION_ID_KEY, 0)
            val position = intent.getIntExtra(TRANSACTION_POSITION_KEY, -1)
            val itemName = intent.getStringExtra(TRANSACTION_NAME_KEY)
            val itemPrice = intent.getIntExtra(TRANSACTION_PRICE_KEY, 0)
            val paymentMethod = intent.getIntExtra(TRANSACTION_PAYMENT_KEY, 0)
            val itemCategory = intent.getIntExtra(TRANSACTION_CATEGORY_KEY, ExpenseCategory.OTHER.ordinal)
            //        val purchaseLocation = intent.getStringExtra("purchaseLocation")
            val purchaseNotes = intent.getStringExtra(TRANSACTION_NOTES_KEY)
            val purchaseDate = intent.getLongExtra(TRANSACTION_DATE_KEY, 0)

            if (position != -1) {
                // Update the TextViews in ArrayList with the new values
                if (itemName != null) {
                    transactionHistoryViewModel.expensesHistory.value!![position].items = itemName
                }
                if (itemPrice != null) {
                    transactionHistoryViewModel.expensesHistory.value!![position].cost = itemPrice
                }
                if (paymentMethod != null) {
                    transactionHistoryViewModel.expensesHistory.value!![position].paymentType = paymentMethod
                }
                if (itemCategory != null) {
                    transactionHistoryViewModel.expensesHistory.value!![position].category = itemCategory
                }
                if (purchaseNotes != null) {
                    transactionHistoryViewModel.expensesHistory.value!![position].note = purchaseNotes
                }
                if (purchaseDate != null) {
                    transactionHistoryViewModel.expensesHistory.value!![position].epochDate = purchaseDate
                }
            }
        }
        println("TRACE: IF STATEMENT HAS ENDED")
    }

    // Begin Activity to create a new transaction
    fun onAddNewTransactionClick(view: View) {
        addTransactionButton = findViewById(R.id.addNewTransactionButton)
        val createTransactionIntent = Intent(this, AddTransactionActivity::class.java)
        startActivity(createTransactionIntent)
    }
}
