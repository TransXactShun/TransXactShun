package com.example.transxactshun.transactions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.transxactshun.R

/**
 * An adapter loads the information to be displayed from a data source,
 * such as an array or database query, and creates a view for each item.
 * Then it inserts the views into the ListView.
 *
 * Reference: https://www.raywenderlich.com/155-android-listview-tutorial-with-kotlin
 */
class TransactionAdapter(context: Context, private val dataArray: Array<Transaction>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    // Returns the size of the dataArray
    override fun getCount(): Int {
        return dataArray.size
    }

    // Returns the a Transaction's ID
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Returns the Transaction at the specified position of the array
    override fun getItem(position: Int): Any {
        return dataArray[position]
    }

    // Returns an inflater with TextView field's filled with transaction details
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the view for a Transaction
        val rowView = inflater.inflate(R.layout.transaction_list_item, parent, false)

        // Get transaction elements
        val itemName = rowView.findViewById(R.id.itemName) as TextView
        val itemPrice = rowView.findViewById(R.id.itemPrice) as TextView
        val paymentMethod = rowView.findViewById(R.id.paymentMethod) as TextView
        val itemCategory = rowView.findViewById(R.id.itemCategory) as TextView
//        val purchaseLocation = rowView.findViewById(R.id.purchaseLocation) as TextView
        val purchaseNotes = rowView.findViewById(R.id.purchaseNotes) as TextView
        val purchaseDate = rowView.findViewById(R.id.purchaseDate) as TextView

        // Transaction for a row
        val transaction = getItem(position) as Transaction

        // Update the row's TextViews
        itemName.text = transaction.itemName
        itemPrice.text = transaction.itemPrice
        paymentMethod.text = transaction.paymentMethod
//        purchaseLocation.text = transaction.purchaseLocation
        itemCategory.text = transaction.itemCategory
        purchaseNotes.text = transaction.purchaseNotes
        purchaseDate.text = transaction.purchaseDate

        return rowView
    }
}
