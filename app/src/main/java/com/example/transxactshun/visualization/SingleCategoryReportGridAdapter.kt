package com.example.transxactshun.visualization

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.transxactshun.R
import com.example.transxactshun.database.ExpenseCategory
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
        val categoryImage: ImageView = itemView.findViewById(R.id.category_image)
        val vendorText: TextView = itemView.findViewById(R.id.vendor)
        val costText: TextView = itemView.findViewById(R.id.cost)
        /**
         * Intended to act as a factory for creating ViewHolders
         */
        companion object {
            fun from(parent: ViewGroup, onClick: (Int) -> Unit): SingleCategoryReportViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.visualization_single_expense_with_category_item_layout, parent,false)
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
        holder.dateText.text = VisualizationUtil.millisecondsToDateFormat(item.date)
        holder.vendorText.text = item.vendor
        val costString = VisualizationUtil.currencyFormat(item.cost)
        holder.costText.text = costString
        val icon = getIcon(item.category)
        holder.categoryImage.setImageResource(icon)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return expenses.size
    }

    /**
     *
     */
    private fun getIcon(category: ExpenseCategory): Int {
        return when (category) {
            ExpenseCategory.GROCERY -> R.drawable.icon_grocery
            ExpenseCategory.RESTAURANTS -> R.drawable.icon_restaurant
            ExpenseCategory.ENTERTAINMENT -> R.drawable.icon_entertainment
            ExpenseCategory.OFFICE -> R.drawable.icon_office
            ExpenseCategory.PERSONAL -> R.drawable.icon_personal
            ExpenseCategory.TRAVEL -> R.drawable.icon_travel
            ExpenseCategory.RECURRING -> R.drawable.icon_recurring
            ExpenseCategory.TECHNOLOGY -> R.drawable.icon_technology
            ExpenseCategory.CONSTRUCTION -> R.drawable.icon_construction
            ExpenseCategory.HOME -> R.drawable.icon_home
            ExpenseCategory.TRANSPORTATION -> R.drawable.icon_transportation
            ExpenseCategory.MEDICAL -> R.drawable.icon_medical
            ExpenseCategory.EDUCATION -> R.drawable.icon_education
            ExpenseCategory.FINANCIAL -> R.drawable.icon_financial
            ExpenseCategory.OTHER -> R.drawable.icon_other
            else -> R.drawable.icon_other
        }
    }
}