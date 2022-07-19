package com.example.transxactshun.visualization

import androidx.lifecycle.*
import com.example.transxactshun.database.ExpensesDatabaseEntry
import com.example.transxactshun.database.ExpensesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class VisualizationViewModel(private val repository: ExpensesRepository) : ViewModel() {
    val expensesHistory: LiveData<List<ExpensesDatabaseEntry>> = repository.entireExpensesHistory.asLiveData()
    val activeGraphView = MutableLiveData<VisualizationGraphViews>(VisualizationGraphViews.DAILY)
    val activeCalendarView = MutableLiveData<VisualizationCalendarViews>(VisualizationCalendarViews.DAILY)

    fun addFakeEntries() {
        CoroutineScope(IO).launch {
            repository.insert(
            ExpensesDatabaseEntry(
                cost = 15,
                items = ByteArray(0),
                epochDate = 0,
                paymentType = 0,
                note = "",
                category = 0,
                vendor = "test"
            ))
            repository.insert(ExpensesDatabaseEntry(
                cost = 25,
                items = ByteArray(0),
                epochDate = 1,
                paymentType = 0,
                note = "",
                category = 1,
                vendor = "test"
            ))
            repository.insert(ExpensesDatabaseEntry(
                cost = 20,
                items = ByteArray(0),
                epochDate = 2,
                paymentType = 0,
                note = "",
                category = 2,
                vendor = "test"
            ))
            repository.insert(ExpensesDatabaseEntry(
                cost = 15,
                items = ByteArray(0),
                epochDate = 3,
                paymentType = 0,
                note = "",
                category = 3,
                vendor = "test"
            ))
        }
    }

    fun deleteAll() {
        repository.deleteAll()
    }
}

class VisualizationViewModelFactory(private val repository: ExpensesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisualizationViewModel::class.java))
            return VisualizationViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}