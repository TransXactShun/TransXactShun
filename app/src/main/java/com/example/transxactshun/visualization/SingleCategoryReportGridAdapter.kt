package com.example.transxactshun.visualization

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.transxactshun.R
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class SingleCategoryReportGridAdapter(
    private val expenses: List<SingleExpenseSummary>,
    private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<SingleCategoryReportGridAdapter.SingleCategoryReportViewHolder>() {

    /**
     * Internal ViewHolder class containing the view for an individual item
     * Inherits from RecyclerView.ViewHolder and View.OnClickListener
     * @param itemView - the associated individual view layout
     * @param onItemClick - a callback function to be assigned to this ViewHolder
     */
    class SingleCategoryReportViewHolder(itemView: View, val onItemClick: (Int) -> Unit): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val dateText: TextView = itemView.findViewById(R.id.date)
        val vendorText: TextView = itemView.findViewById(R.id.vendor)
        val costText: TextView = itemView.findViewById(R.id.cost)
        /**
         * Intended to act as a factory for creating ViewHolders
         */
        companion object {
            fun from(parent: ViewGroup, onClick: (Int) -> Unit): SingleCategoryReportViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.visualization_single_expense_item_layout, parent,false)
                return SingleCategoryReportViewHolder(view, onClick)
            }
        }

        init {
            itemView.setOnClickListener(this)
        }

        /**
         * Overridden onClick function
         * Assign the callback function to onClick event listener
         */
        override fun onClick(v: View?) {
            val position = adapterPosition
            onItemClick(position)
        }
    }

    /**
     * Overridden onCreateViewHolder function
     * @param parent – The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType – The view type of the new View.
     * @return a new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleCategoryReportViewHolder {
        return SingleCategoryReportViewHolder.from(parent, onItemClick)
    }

    /**
     * Overridden onBindViewHolder function
     * Sets the image of the ViewHolder based on position. The images are provided in ImageGroup.
     * @param holder – The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position – The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: SingleCategoryReportViewHolder, position: Int) {
        val item = expenses[position]
        val unformattedDate = Instant.ofEpochMilli(item.date).atZone(ZoneOffset.UTC).toLocalDate()
        val dateFormatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.CANADA)
        holder.dateText.text = dateFormatter.format(unformattedDate)
        holder.vendorText.text = item.vendor
        val costString = VisualizationUtil.currencyFormat(item.cost)
        holder.costText.text = costString
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return expenses.size
    }
}