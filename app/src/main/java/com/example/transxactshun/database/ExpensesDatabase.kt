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
    GIFT("Gift Card/Certificate"),
}

enum class ExpenseCategory(val displayValue: String) {
    GROCERY("Grocery"),
    RESTAURANTS("Restaurants"),
    ENTERTAINMENT("Entertainment"),
    TRAVEL("Travel"),
    RECURRING("Recurring Bill Payments"),
    PERSONAL("Personal"),
    TECHNOLOGY("Technology"),
    TRANSPORTATION("Transportation"),
    MEDICAL("Medical"),
    HOME("Home"),
    EDUCATION("Education"),
    CONSTRUCTION("Construction"),
    OFFICE("Office"),
    FINANCIAL("Financial"),
    OTHER("Other");

    companion object {
        fun getExpenseCategoryFrom(value: String): ExpenseCategory {
            return when (value) {
                "Grocery" -> GROCERY
                "Restaurants" -> RESTAURANTS
                "Entertainment" -> ENTERTAINMENT
                "Travel" -> TRAVEL
                "Recurring Bill Payments" -> RECURRING
                "Technology" -> TECHNOLOGY
                "Personal" -> PERSONAL
                "Transportation" -> TRANSPORTATION
                "Medical" -> MEDICAL
                "Home" -> HOME
                "Education" -> EDUCATION
                "Construction" -> CONSTRUCTION
                "Office" -> OFFICE
                "Financial" -> FINANCIAL
                "Other" -> OTHER
                else -> OTHER
            }
        }
    }
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