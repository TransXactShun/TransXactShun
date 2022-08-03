package com.example.transxactshun.transactions

import android.R.attr.button
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.transxactshun.R
import java.sql.SQLOutput


class EditTransactionActivity: AppCompatActivity() {
    // EditText fields
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var paymentMethodEditText: EditText
    private lateinit var itemCategoryEditText: EditText
//    private lateinit var purchaseLocationEditText: EditText
    private lateinit var purchaseNotesEditText: EditText
    private lateinit var purchaseDateEditText: EditText

    // Buttons
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        // Initialize Buttons
        saveButton = findViewById(R.id.saveTransactionEdit)
        cancelButton = findViewById(R.id.cancelTransactionEdit)
        deleteButton = findViewById(R.id.deleteTransaction)

        // Initialize EditText fields
        itemNameEditText = findViewById(R.id.editItemName)
        itemPriceEditText = findViewById(R.id.editItemPrice)
        paymentMethodEditText = findViewById(R.id.editPaymentMethod)
        itemCategoryEditText = findViewById(R.id.editItemCategory)
//        purchaseLocationEditText = findViewById(R.id.editPurchaseLocation)
        purchaseNotesEditText = findViewById(R.id.editPurchaseNotes)
        purchaseDateEditText = findViewById(R.id.editPurchaseDate)

        val position = intent.getIntExtra("position", -1)
        val itemName = intent.getStringExtra("itemName")
        val itemPrice = intent.getStringExtra("itemPrice")
        val paymentMethod = intent.getStringExtra("paymentMethod")
        val itemCategory = intent.getStringExtra("itemCategory")
//        val purchaseLocation = intent.getStringExtra("purchaseLocation")
        val purchaseNotes = intent.getStringExtra("purchaseNotes")
        val purchaseDate = intent.getStringExtra("purchaseDate")

        itemNameEditText.setText(itemName)
        itemPriceEditText.setText(itemPrice)
        paymentMethodEditText.setText(paymentMethod)
        itemCategoryEditText.setText(itemCategory)
//        purchaseLocationEditText.setText(purchaseLocation)
        purchaseNotesEditText.setText(purchaseNotes)
        purchaseDateEditText.setText(purchaseDate)

        saveButton.setOnClickListener() {
            // Obtain current EditText values
            val itemNameEdit: String = itemNameEditText.text.toString()
            println("TRACE: CHECKING NAME STRING IN EDIT TEXT")
            println(itemNameEdit)
            val itemPriceEdit: String = itemPriceEditText.text.toString()
            val purchaseDateEdit: String = purchaseDateEditText.text.toString()
            val paymentMethodEdit: String = paymentMethodEditText.text.toString()
            val purchaseNotesEdit: String = purchaseNotesEditText.text.toString()
            val itemCategoryEdit: String = itemCategoryEditText.text.toString()

            // Send values to TransactionHistoryActivity
            val saveEditsIntent = Intent(this, TransactionHistoryActivity::class.java)
            saveEditsIntent.putExtra("EditTransactionCalled", true)
            saveEditsIntent.putExtra("position", position)
            saveEditsIntent.putExtra("itemName", itemNameEdit)
            saveEditsIntent.putExtra("itemPrice", itemPriceEdit)
            saveEditsIntent.putExtra("purchaseDate", purchaseDateEdit)
            saveEditsIntent.putExtra("paymentMethod", paymentMethodEdit)
            saveEditsIntent.putExtra("purchaseNotes", purchaseNotesEdit)
            saveEditsIntent.putExtra("itemCategory", itemCategoryEdit)
            startActivity(saveEditsIntent)
//            finish()
        }

        // TODO: Not important as users can click the back button
        cancelButton.setOnClickListener() {
            // Exit activity
            finish()
        }

        deleteButton.setOnClickListener() {
            fun onClick(v: View?) {

            }
        }
    }
}