package com.example.transxactshun.transactions

import android.content.ClipData.newIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.transxactshun.R

class TransactionHistoryActivity : AppCompatActivity() {
    private lateinit var transactionListView: ListView
    private lateinit var addTransactionButton: Button
    private lateinit var transactionSelected: Transaction

    // Creating dummy transactions with the constructor
    val transactionList: Array<Transaction> = arrayOf(
        Transaction("Peanut butter",
            "$15",
            "Tuesday, July 19 at 9:30 AM",
            "Credit Card",
            "It was crunchy",
            "Food"),
        Transaction("Jam",
            "$8",
            "Thursday, July 21 at 4:50 PM",
            "Debit Card",
            "Very sweet",
            "Food"),
        Transaction("Jam",
            "$8",
            "Thursday, July 21 at 4:50 PM",
            "Debit Card",
            "Very sweet",
            "Food"),
        Transaction("Jam",
            "$8",
            "Thursday, July 21 at 4:50 PM",
            "Debit Card",
            "Very sweet",
            "Food"),
        Transaction("Jam",
            "$8",
            "Thursday, July 21 at 4:50 PM",
            "Debit Card",
            "Very sweet",
            "Food"),
        Transaction("Jam",
            "$8",
            "Thursday, July 21 at 4:50 PM",
            "Debit Card",
            "Very sweet",
            "Food")
    )

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



        // Uses the TransactionAdapter class to create list items
        val adapter = TransactionAdapter(this, transactionList)
        transactionListView.adapter = adapter

        // Transactions are clickable by starting the EditTransactionActivity
        transactionListView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            // Get the selected Transaction
            transactionSelected = transactionList.get(position)

            // Save the values of the selected Transaction
            val itemName: String = transactionSelected.itemName
            val itemPrice: String = transactionSelected.itemPrice
            val purchaseDate: String = transactionSelected.purchaseDate
            val paymentMethod: String = transactionSelected.paymentMethod
            val purchaseNotes: String = transactionSelected.purchaseNotes
            val itemCategory: String = transactionSelected.itemCategory

            // Send the values of the selected Transaction to the next activity
            val editTransactionIntent = Intent(this, EditTransactionActivity::class.java)
            editTransactionIntent.putExtra("position", position)
            editTransactionIntent.putExtra("itemName", itemName)
            editTransactionIntent.putExtra("itemPrice", itemPrice)
            editTransactionIntent.putExtra("purchaseDate", purchaseDate)
            editTransactionIntent.putExtra("paymentMethod", paymentMethod)
            editTransactionIntent.putExtra("purchaseNotes", purchaseNotes)
            editTransactionIntent.putExtra("itemCategory", itemCategory)
            startActivity(editTransactionIntent)
        }
    }

    // When EditTransactionActivity starts, the current activity is onPause() and onStop().
    // onResume() be called once it ends
    override fun onResume() {
        super.onResume()
        // If EditTransactionHistory was called and Save was used
        val editTransactionCalled = intent.getBooleanExtra("EditTransactionCalled", false) // Returns a Boolean value"
        println("TRACE: CHECKING IF STATEMENT")
        println(editTransactionCalled)
        println(intent.getStringExtra("itemName"))
        if (editTransactionCalled) {
            println("TRACE: IF STATEMENT WAS HIT")
            // Save was called retrieve new values sent by putExtra()
            val position = intent.getIntExtra("position", -1)
            val itemName = intent.getStringExtra("itemName")
            val itemPrice = intent.getStringExtra("itemPrice")
            val paymentMethod = intent.getStringExtra("paymentMethod")
            val itemCategory = intent.getStringExtra("itemCategory")
            //        val purchaseLocation = intent.getStringExtra("purchaseLocation")
            val purchaseNotes = intent.getStringExtra("purchaseNotes")
            val purchaseDate = intent.getStringExtra("purchaseDate")

            if (position != -1) {
                // Update the TextViews in ArrayList with the new values
                if (itemName != null) {
                    transactionList[position].itemName = itemName
                }
                if (itemPrice != null) {
                    transactionList[position].itemPrice = itemPrice
                }
                if (paymentMethod != null) {
                    transactionList[position].paymentMethod = paymentMethod
                }
                if (itemCategory != null) {
                    transactionList[position].itemCategory = itemCategory
                }
                if (purchaseNotes != null) {
                    transactionList[position].purchaseNotes = purchaseNotes
                }
                if (purchaseDate != null) {
                    transactionList[position].purchaseDate = purchaseDate
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
