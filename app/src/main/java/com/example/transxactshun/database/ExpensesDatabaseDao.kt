package com.example.transxactshun.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Dao
interface ExpensesDatabaseDao {
    @Insert
    suspend fun insertExpenseEntry(entry: ExpensesDatabaseEntry)

    @Query("UPDATE ${ExpensesDatabase.EXPENSES_TABLE_NAME} SET " +
            "cost = :cost, items = :items, date = :epochDate, payment_type = :paymentType, note = :note, category = :category " +
            "WHERE id = :id"
    )
    fun adjustEntry(id: Long, cost: Int, items: String, epochDate: Long, paymentType: Int, note: String, category: Int)

    @Query("SELECT * FROM ${ExpensesDatabase.EXPENSES_TABLE_NAME}")
    fun getEntireExpensesHistory(): Flow<List<ExpensesDatabaseEntry>>

    @Query("SELECT * FROM ${ExpensesDatabase.EXPENSES_TABLE_NAME} ORDER BY date")
    fun getEntireExpensesHistorySortedByDate(): Flow<List<ExpensesDatabaseEntry>>

    @Query("SELECT * FROM ${ExpensesDatabase.EXPENSES_TABLE_NAME} WHERE date >= :startDate ORDER BY date")
    fun getExpensesHistoryStartingFrom(startDate: Long): Flow<List<ExpensesDatabaseEntry>>

    @Query("SELECT * FROM ${ExpensesDatabase.EXPENSES_TABLE_NAME} WHERE date >= :startDate AND date <= :endDate ORDER BY date")
    fun getExpensesHistoryBetween(startDate: Long, endDate: Long): Flow<List<ExpensesDatabaseEntry>>

    @Query("DELETE FROM ${ExpensesDatabase.EXPENSES_TABLE_NAME}")
    fun deleteAll()

    @Query("DELETE FROM ${ExpensesDatabase.EXPENSES_TABLE_NAME} WHERE id = :key")
    fun deleteEntry(key: Long)
}