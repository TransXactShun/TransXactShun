package com.example.transxactshun.transactions

// TODO: add _purchaseLocation: String or GPS data location
class Transaction(
    _itemName: String, _itemPrice: String, _purchaseDate: String, _paymentMethod: String,
    _purchaseNotes: String, _itemCategory: String
) {
    var itemName: String
    var itemPrice: String
    var paymentMethod: String
    var itemCategory: String
    var purchaseNotes: String
    var purchaseDate: String

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