package com.example.transxactshun.transactions

import android.widget.ArrayAdapter

// TODO: add _purchaseLocation: String
class TransactionDetails(_itemName: String, _itemPrice: String, _purchaseDate: String, _paymentMethod: String,
                         _purchaseNotes: String, _itemCategory: String) { //
    public val itemName: String
    public val itemPrice: String
    public val purchaseDate: String // TODO: Change date to be of type Date later
    public val paymentMethod: String
    public val purchaseNotes: String
    public val itemCategory: String
//    private val purchaseLocation: String

    // Constructor
    init {
        itemName = _itemName
        itemPrice = _itemPrice
        purchaseDate = _purchaseDate
        paymentMethod = _paymentMethod
        purchaseNotes = _purchaseNotes
        itemCategory = _itemCategory
    }
}