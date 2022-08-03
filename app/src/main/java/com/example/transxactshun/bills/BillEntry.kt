package com.example.transxactshun.bills

data class BillEntry(
    var payeeName: String? = null,
    var memo: String? = null,
    var amount: Double = 0.0,
    var reminderDate: Long? = 0L
)
