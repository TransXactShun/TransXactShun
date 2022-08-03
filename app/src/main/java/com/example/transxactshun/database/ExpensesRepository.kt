package com.example.transxactshun.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExpensesRepository(private val expensesDatabaseDao: ExpensesDatabaseDao) {
    val entireExpensesHistory: Flow<List<ExpensesDatabaseEntry>> = expensesDatabaseDao.getEntireExpensesHistorySortedByDate()

    /**
     * Inserts a single entry into the database
     * @param entry - a ExpensesDatabaseEntry object
     */
    fun insert(entry: ExpensesDatabaseEntry) {
        CoroutineScope(IO).launch {
            expensesDatabaseDao.insertExpenseEntry(entry)
        }
    }

    /**
     * Query the database for results from a specific date
     * @param startDate - the earliest date that results should contain. Value is in milliseconds (epoch)
     * @return a list of results that meet the requirement
     */
    fun getExpensesStartingFrom(startDate: Long): Flow<List<ExpensesDatabaseEntry>> {
        return expensesDatabaseDao.getExpensesHistoryStartingFrom(startDate)
    }

    /**
     * Query the database for results between specific dates
     * @param startDate - the earliest date that results should contain. Value is in milliseconds (epoch)
     * @param endDate - the latest date that results should contain. Value is in milliseconds (epoch)
     * @return a list of results that meet the requirement
     */
    fun getExpensesBetween(startDate: Long, endDate: Long): Flow<List<ExpensesDatabaseEntry>> {
        return expensesDatabaseDao.getExpensesHistoryBetween(startDate, endDate)
    }

    /**
     * Delete a single entry from the database
     * @param id - the id of the desired entry to delete
     */
    fun delete(id: Long) {
        CoroutineScope(IO).launch {
            expensesDatabaseDao.deleteEntry(id)
        }
    }

    /**
     * Delete all entries from the database
     */
    fun deleteAll() {
        CoroutineScope(IO).launch {
            expensesDatabaseDao.deleteAll()
        }
    }
}