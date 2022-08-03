package com.example.transxactshun.bills

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.transxactshun.R
import java.text.SimpleDateFormat
import java.util.*

class BillListAdapter(
    private val context: Context,
    private var reminderList: List<BillEntry>
) : BaseAdapter() {
    override fun getCount(): Int {
        return reminderList.size
    }

    override fun getItem(position: Int): Any {
        return reminderList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.reminder_list_adapter, null)

        var billObject: BillEntry = reminderList[position]

        // setup UI elements
        var payeeNameTextView: TextView = view.findViewById(R.id.textViewReminderName)
        var amountTextView: TextView = view.findViewById(R.id.textViewReminderAmount)
        var memoTextView: TextView = view.findViewById(R.id.textViewReminderMemo)
        var dateTextView: TextView = view.findViewById(R.id.textViewReminderDate)

        // set values
        payeeNameTextView.text = "Payee: ${billObject.payeeName}"
        amountTextView.text = "Amount: $ ${billObject.amount.toString()}"
        memoTextView.text = "Memo: ${billObject.memo}"
        val dateValue = billObject.reminderDate
        var calendar: Calendar = Calendar.getInstance()
        if (dateValue != null) {
            calendar.timeInMillis = dateValue
        }
        var dateFormat: String? = SimpleDateFormat("HH:mm:ss MMMM d yyyy").format(
            calendar.time
        )

        dateTextView.text = "Remind On: ${dateFormat}"

        return view
    }

    fun replace(updatedBillList: List<BillEntry>) {
        reminderList = updatedBillList
    }

}