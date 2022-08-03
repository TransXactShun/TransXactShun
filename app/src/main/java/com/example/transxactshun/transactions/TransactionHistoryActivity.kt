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

        // Uses the TransactionAdapter class to create list items
        val adapter = TransactionAdapter(this, transactionList)
        transactionListView.adapter = adapter

        // TODO: Let transactions be clickable by starting the EditTransactionActivity
//        transactionListView.setOnItemClickListener { item: AdapterView<*>, textView: View, position: Int, id: Long ->
//            val transaction = transactionList[position]
//            val editTransactionIntent = EditTransactionActivity.newIntent(this, transaction)
//            startActivity(editTransactionIntent)
//        }
    }

    // Begin Activity to create a new transaction
    fun onAddNewTransactionClick(view: View) {
        addTransactionButton = findViewById(R.id.addNewTransactionButton)
        val createTransactionIntent = Intent(this, AddTransactionActivity::class.java)
        startActivity(createTransactionIntent)
    }
}