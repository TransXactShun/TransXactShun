package com.example.transxactshun.menu

import androidx.lifecycle.ViewModel
import com.example.transxactshun.R

class MenuViewModel : ViewModel() {

    val imageIconArray: ArrayList<Int> = arrayListOf(
        R.drawable.icons_transaction,
        R.drawable.icons_graphs,
        R.drawable.icons_bill,
        R.drawable.icons_budget,
        R.drawable.icons_report,
    )

    val menuItemArray: ArrayList<String> = arrayListOf(
        "Transactions",
        "Spending Trends (Graphs)",
        "Bill Reminder",
        "Set Monthly Budget",
        "Share Receipts"
    )
}