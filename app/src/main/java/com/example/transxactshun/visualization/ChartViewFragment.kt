package com.example.transxactshun.visualization

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.*
import androidx.recyclerview.widget.RecyclerView
import com.example.transxactshun.R
import com.example.transxactshun.database.ExpenseCategory
import com.example.transxactshun.database.ExpensesDatabase
import com.example.transxactshun.database.ExpensesRepository
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.PieChartView
import kotlin.collections.ArrayList

class ChartViewFragment: Fragment() {
    private val TAG = "ChartViewFragment"
    private val CATEGORY_SELECTED_KEY = "Chart_View_Category_Selected_Key"
    private lateinit var appContext: Context
    private lateinit var pieChart: PieChartView
    private lateinit var dateRangeText: TextView
    private lateinit var categoryReportHeader: TextView
    private lateinit var categoryReportView: RecyclerView
    private val viewModel: VisualizationViewModel by activityViewModels { VisualizationViewModelFactory(
        ExpensesRepository(ExpensesDatabase.getInstance(requireContext()).expensesDatabaseDao)
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(IO).launch {
            appContext = requireContext().applicationContext
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ui = inflater.inflate(R.layout.visualization_piechart_layout, container, false)

        pieChart = ui.findViewById(R.id.pie_chart)
        dateRangeText = ui.findViewById(R.id.date_range)
        categoryReportHeader = ui.findViewById(R.id.report_header)
        categoryReportView = ui.findViewById(R.id.report_summary)
        onDateSelectedSetText(viewModel.startDate.value!!, viewModel.endDate.value!!)
        val underlineText = SpannableString("All Categories")
        underlineText.setSpan(UnderlineSpan(), 0, underlineText.length, 0)
        categoryReportHeader.text = underlineText
        if (savedInstanceState != null) {
            val savedString = savedInstanceState.getString(CATEGORY_SELECTED_KEY)
            if (savedString != null) categoryReportHeader.text = savedString
        }

        // If start date changes, update all visualizations
        viewModel.startDate.observe(viewLifecycleOwner) {
            onDateSelectedSetText(it, viewModel.endDate.value!!)
            val (categoryExpenseMap, totalExpense, expenseReport) = viewModel.getExpensesByCategoryBetween(it, viewModel.endDate.value!!)
            buildPieChart(categoryExpenseMap, totalExpense)
            buildReportSummary(expenseReport)
        }
        // If end date changes, update all visualizations
        viewModel.endDate.observe(viewLifecycleOwner) {
            onDateSelectedSetText(viewModel.startDate.value!!, it)
            val (categoryExpenseMap, totalExpense, expenseReport) = viewModel.getExpensesByCategoryBetween(viewModel.startDate.value!!, it)
            buildPieChart(categoryExpenseMap, totalExpense)
            buildReportSummary(expenseReport)
        }
        // If expenses changes, update all visualizations
        viewModel.expensesHistory.observe(viewLifecycleOwner) {
            val (categoryExpenseMap, totalExpense, expenseReport) = viewModel.getExpensesByCategoryBetween(viewModel.startDate.value!!, viewModel.endDate.value!!)
            buildPieChart(categoryExpenseMap, totalExpense)
            buildReportSummary(expenseReport)
        }
        // If category changes, update only the report (recycler view)
        viewModel.pieChartSelectedCategory.observe(viewLifecycleOwner) {
            var displayText = it?.displayText ?: "All Categories"
            val underlineText = SpannableString(displayText)
            underlineText.setSpan(UnderlineSpan(), 0, underlineText.length, 0)
            categoryReportHeader.text = underlineText

            val expenseReport = viewModel.getSingleExpenseCategorySummaryBetween(viewModel.startDate.value!!, viewModel.endDate.value!!, it)
            buildReportSummary(expenseReport)
        }

        pieChart.onValueTouchListener = ChartOnTouchListener()

        dateRangeText.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            datePicker.setSelection(androidx.core.util.Pair(viewModel.startDate.value!!, viewModel.endDate.value!!))
            val dialog = datePicker.build()
            dialog.addOnPositiveButtonClickListener {
                viewModel.startDate.value = it.first
                viewModel.endDate.value = it.second
            }
            dialog.show(requireActivity().supportFragmentManager, dialog.toString())
        }

        return ui
    }

    /**
     * Builds the data for pie chart and updates the pie chart
     * @param categoryExpenseMap - a map of int, int with the first value is the category (based on enum class ExpenseCategory)
     *                             and the second value is the total expense in that category
     * @param totalExpense - an int representing the total expenses for every category in the map
     */
    private fun buildPieChart(categoryExpenseMap: Map<ExpenseCategory, Int>, totalExpense: Int) {
        CoroutineScope(IO).launch {
            // Divide up the categories into slices
            val pieChartSlices = ArrayList<SliceValue>()
            categoryExpenseMap.forEach {
                val categorySlice = SliceValue((it.value).toFloat(), getCategoryColour(it.key))
                val categoryString = it.key.displayText
                val categoryCost = VisualizationUtil.currencyFormat(it.value)
                val categoryLabel = "${categoryString}: $categoryCost"
                categorySlice.setLabel(categoryLabel)
                pieChartSlices.add(categorySlice)
            }

            // Set visualization extras
            val pieData = PieChartData(pieChartSlices)
            pieData.setHasLabels(true)
            withContext(Main) {
                pieChart.pieChartData = pieData
                pieChart.isValueSelectionEnabled = true
            }
        }
    }

    /**
     * Builds the data for the report summary and creates the adapter for the recycler view
     * @param expenseSummary - an arraylist of SingleExpenseSummary objects
     */
    private fun buildReportSummary(expenseSummary: ArrayList<SingleExpenseSummary>) {
        CoroutineScope(IO).launch {
            if (viewModel.expensesHistory.value != null) {
                val reportAdapter = SingleCategoryReportGridAdapter(expenseSummary) {
                        position -> Log.i(TAG, "$position selected")
                }
                withContext(Main) {
                    categoryReportView.adapter = reportAdapter
                }
            }
        }
    }


    /**
     * Takes 2 dates and formats them to display them as representing a date range
     * Also provides an underline to the text
     * @param start - the start dates of the range in milliseconds
     * @param end - the end dates of the range in milliseconds
     */
    // https://stackoverflow.com/questions/5645789/how-to-set-underline-text-on-textview
    // https://stackoverflow.com/questions/7953725/how-to-convert-milliseconds-to-date-format-in-android
    private fun onDateSelectedSetText(start: Long, end: Long) {
        val startDateText = VisualizationUtil.millisecondsToDateFormat(start)
        val endDateText = VisualizationUtil.millisecondsToDateFormat(end)
        val underlineText = SpannableString("$startDateText - $endDateText")
        underlineText.setSpan(UnderlineSpan(), 0, underlineText.length, 0)
        dateRangeText.text = underlineText
    }

    private fun getCategoryColour(category: ExpenseCategory): Int {
        return when (category) {
            ExpenseCategory.GROCERY -> { ChartUtils.COLOR_GREEN }
            ExpenseCategory.RESTAURANTS -> { ChartUtils.COLOR_BLUE }
            ExpenseCategory.ENTERTAINMENT -> { ChartUtils.COLOR_ORANGE }
            ExpenseCategory.OFFICE -> { ChartUtils.COLOR_RED }
            ExpenseCategory.PERSONAL -> { ChartUtils.COLOR_VIOLET }
            ExpenseCategory.TRAVEL -> { ChartUtils.darkenColor(ChartUtils.COLOR_BLUE) }
            ExpenseCategory.RECURRING -> { ChartUtils.darkenColor(ChartUtils.COLOR_RED) }
            ExpenseCategory.TECHNOLOGY -> { ChartUtils.darkenColor(ChartUtils.COLOR_VIOLET) }
            ExpenseCategory.CONSTRUCTION -> { ChartUtils.darkenColor(ChartUtils.COLOR_ORANGE) }
            ExpenseCategory.HOME -> { ChartUtils.darkenColor(ChartUtils.COLOR_GREEN) }
            ExpenseCategory.TRANSPORTATION -> { ChartUtils.darkenColor( ChartUtils.darkenColor(ChartUtils.COLOR_ORANGE) ) }
            ExpenseCategory.MEDICAL -> { ChartUtils.darkenColor( ChartUtils.darkenColor(ChartUtils.COLOR_RED) ) }
            ExpenseCategory.EDUCATION -> { ChartUtils.darkenColor( ChartUtils.darkenColor(ChartUtils.COLOR_BLUE) ) }
            ExpenseCategory.FINANCIAL -> { ChartUtils.darkenColor( ChartUtils.darkenColor(ChartUtils.COLOR_GREEN) ) }
            ExpenseCategory.OTHER -> { ChartUtils.darkenColor( ChartUtils.darkenColor(ChartUtils.COLOR_VIOLET) ) }
            else -> { ChartUtils.darkenColor( ChartUtils.darkenColor(ChartUtils.COLOR_VIOLET) ) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class ChartOnTouchListener: PieChartOnValueSelectListener {

        /**
         * Overridden onValueSelected function
         * Triggered when a user selects a slice in the PieChart and updates the report summary
         * to match the category selected by the user
         */
        override fun onValueSelected(arcIndex: Int, value: SliceValue?) {
            if (value != null) {
                val labelString = String(value.labelAsChars)
                val categoryString = labelString.split(":")
                viewModel.pieChartSelectedCategory.value = ExpenseCategory.getExpenseCategoryFrom(categoryString[0])
            }
        }

        /**
         * Called only in chart selection mode when user touch empty space causing value deselection.
         * Note: this method is not called when selection mode is disabled.
         */
        override fun onValueDeselected() {
            viewModel.pieChartSelectedCategory.value = null
        }
    }

}