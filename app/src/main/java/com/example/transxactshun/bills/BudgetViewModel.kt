package com.example.transxactshun.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BudgetViewModel: ViewModel() {

    var currentBudgetAmount = MutableLiveData<Double>()


}