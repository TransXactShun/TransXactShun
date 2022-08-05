package com.example.transxactshun.transactions

import androidx.lifecycle.*
import com.example.transxactshun.database.ExpensesDatabaseEntry
import com.example.transxactshun.database.ExpensesRepository
import java.lang.IllegalArgumentException

class EditTransactionViewModel(private val repository: ExpensesRepository) : ViewModel()  {
    val TAG = "EditTransactionViewModel"
    val expensesHistory: LiveData<List<ExpensesDatabaseEntry>> = repository.entireExpensesHistory.asLiveData()
    val id = MutableLiveData<Long>()
    val position = MutableLiveData<Int>()
    val email = MutableLiveData<String>()
    val cost = MutableLiveData<Int>()
    val items = MutableLiveData<String>()
    val vendor = MutableLiveData<String>()
    val paymentType = MutableLiveData<Int>()
    val category = MutableLiveData<Int>()
    val epochDate = MutableLiveData<Long>()
    val note = MutableLiveData<String>()
    val year = MutableLiveData<Int>()
    val month = MutableLiveData<Int>()
    val day = MutableLiveData<Int>()
    val hour = MutableLiveData<Int>()
    val minute = MutableLiveData<Int>()

    fun adjustEntry(entry: ExpensesDatabaseEntry) {
        repository.adjustEntry(entry)
    }
}

class EditTransactionViewModelFactory(private val repository: ExpensesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTransactionViewModel::class.java))
            return EditTransactionViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}