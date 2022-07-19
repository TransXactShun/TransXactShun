package com.example.transxactshun.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

enum class PaymentType(val string: String) {
    CASH("Cash"),
    DEBIT("Debit"),
    CREDIT("Credit"),
    CHEQUE("Cheque"),
    GIFT("Gift"),
}

enum class ExpenseCategory(val string: String) {
    GROCERY("Grocery"),
    GAS("Gas"),
    RESTAURANTS("Restaurants"),
    DRUG_STORE("Drug Store"),
    ENTERTAINMENT("Entertainment"),
    TRAVEL("Travel"),
    RECURRING("Recurring Bill Payments"),
    TRANSPORTATION("Transportation"),
    RETAIL("Retail"),
    MEDICAL("Medical"),
    CONSTRUCTION("Construction"),
    FURNITURE("Furniture"),
    HOME("Home")
}

@Database(entities = [ExpensesDatabaseEntry::class], version = 1)
abstract class ExpensesDatabase: RoomDatabase() {
    abstract val expensesDatabaseDao: ExpensesDatabaseDao
    companion object {
        const val EXPENSES_TABLE_NAME = "TransXactShun_Table"

        @Volatile
        private var INSTANCE: ExpensesDatabase? = null
        fun getInstance(context: Context): ExpensesDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, ExpensesDatabase::class.java, EXPENSES_TABLE_NAME).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}