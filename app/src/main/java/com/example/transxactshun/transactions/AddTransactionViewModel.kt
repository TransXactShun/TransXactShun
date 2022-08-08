package com.example.transxactshun.transactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class AddTransactionViewModel: ViewModel() {
    var dateTransaction: Calendar? = null

    var currentBudgetAmount = MutableLiveData<Double>()
}