package com.example.transxactshun.transactions

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.transxactshun.R

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
    }

    fun onSaveNewTransaction(): Int {
        // TODO: Not yet implemented
        println("TRACE: Save clicked")

        return 0
    }

    fun onCancelNewTransaction(): Int {
        // TODO: Not yet implemented
        println("TRACE: Cancel clicked")
        return 0
    }
}