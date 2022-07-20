package com.example.transxactshun.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpensesDatabaseDao {
    @Insert
    suspend fun insertExpenseEntry(entry: ExpensesDatabaseEntry)

    @Query("SELECT * FROM ${ExpensesDatabase.EXPENSES_TABLE_NAME}")
    fun getEntireExpensesHistory(): Flow<List<ExpensesDatabaseEntry>>

    @Query("DELETE FROM ${ExpensesDatabase.EXPENSES_TABLE_NAME}")
    fun deleteAll()

    @Query("DELETE FROM ${ExpensesDatabase.EXPENSES_TABLE_NAME} WHERE id = :key")
    fun deleteEntry(key: Long)
}