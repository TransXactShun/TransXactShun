package com.example.transxactshun.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ExpensesDatabase.EXPENSES_TABLE_NAME)
data class ExpensesDatabaseEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name="email", typeAffinity = ColumnInfo.TEXT)
    var email: String,

    @ColumnInfo(name="cost", typeAffinity = ColumnInfo.INTEGER)
    // In cents
    var cost: Int = 0,

    @ColumnInfo(name="items", typeAffinity = ColumnInfo.TEXT)
    var items: String,

    @ColumnInfo(name="date", typeAffinity = ColumnInfo.INTEGER)
    // In milliseconds
    var epochDate: Long = 0L,

    @ColumnInfo(name="payment_type", typeAffinity = ColumnInfo.INTEGER)
    // Should match enum class PaymentType in ExpensesDatabase
    var paymentType: Int,

    @ColumnInfo(name="note", typeAffinity = ColumnInfo.TEXT)
    var note: String,

    @ColumnInfo(name="category", typeAffinity = ColumnInfo.INTEGER)
    // Should match enum class ExpensesCategory in ExpensesDatabase
    var category: Int,

    @ColumnInfo(name="vendor", typeAffinity = ColumnInfo.TEXT)
    var vendor: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpensesDatabaseEntry

        if (id != other.id) return false
        if (email != other.email) return false
        if (cost != other.cost) return false
        if (!items.contentEquals(other.items)) return false
        if (epochDate != other.epochDate) return false
        if (paymentType != other.paymentType) return false
        if (note != other.note) return false
        if (category != other.category) return false
        if (vendor != other.vendor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + cost
        result = 31 * result + items.hashCode()
        result = 31 * result + epochDate.hashCode()
        result = 31 * result + paymentType
        result = 31 * result + note.hashCode()
        result = 31 * result + category
        result = 31 * result + vendor.hashCode()
        return result
    }

}