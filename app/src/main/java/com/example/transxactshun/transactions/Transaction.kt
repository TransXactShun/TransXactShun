package com.example.transxactshun.transactions

// TODO: add _purchaseLocation: String or GPS data location
class Transaction(_itemName: String, _itemPrice: String, _purchaseDate: String, _paymentMethod: String,
                  _purchaseNotes: String, _itemCategory: String) {
    // TODO: Try turning these fields to private and access them via Getters and Setters
    val itemName: String
    val itemPrice: String
    val paymentMethod: String
    val itemCategory: String
//    private val purchaseLocation: String
    val purchaseNotes: String
    val purchaseDate: String // TODO: Change date to be of type 'Date' later

    // Constructor
    init {
        itemName = _itemName
        itemPrice = _itemPrice
        paymentMethod = _paymentMethod
        itemCategory = _itemCategory
        purchaseNotes = _purchaseNotes
        purchaseDate = _purchaseDate
    }
}