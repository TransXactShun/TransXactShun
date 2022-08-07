package com.example.transxactshun.transactions

import androidx.lifecycle.*
import com.example.transxactshun.database.ExpensesDatabaseEntry
import com.example.transxactshun.database.ExpensesRepository
import java.lang.IllegalArgumentException

class TransactionHistoryViewModel(private val repository: ExpensesRepository) : ViewModel()  {
    val TAG = "TransactionHistoryViewModel"
    val expensesHistory: LiveData<List<ExpensesDatabaseEntry>> = repository.entireExpensesHistory.asLiveData()
}

class TransactionHistoryViewModelFactory(private val repository: ExpensesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionHistoryViewModel::class.java))
            return TransactionHistoryViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}