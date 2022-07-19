package com.example.transxactshun.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExpensesRepository(private val expensesDatabaseDao: ExpensesDatabaseDao) {
    val entireExpensesHistory: Flow<List<ExpensesDatabaseEntry>> = expensesDatabaseDao.getEntireExpensesHistory()

    fun insert(entry: ExpensesDatabaseEntry) {
        CoroutineScope(IO).launch {
            expensesDatabaseDao.insertExpenseEntry(entry)
        }
    }

    fun delete(id: Long) {
        CoroutineScope(IO).launch {
            expensesDatabaseDao.deleteEntry(id)
        }
    }

    fun deleteAll() {
        CoroutineScope(IO).launch {
            expensesDatabaseDao.deleteAll()
        }
    }
}