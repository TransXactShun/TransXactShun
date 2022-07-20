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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.*
import com.example.transxactshun.R
import com.example.transxactshun.database.ExpensesDatabase
import com.example.transxactshun.database.ExpensesRepository
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.PieChartView
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class GraphViewFragment(): Fragment() {
    private val TAG = "GraphViewFragment"
    private lateinit var appContext: Context
    private lateinit var pieChart: PieChartView
    private lateinit var dateRangeText: TextView

    // For test
//    private lateinit var btnAdd: Button
//    private lateinit var btnDeleteAll: Button
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
        val ui = inflater.inflate(R.layout.graph_piechart_layout, container, false)
        pieChart = ui.findViewById(R.id.pie_chart)
//        btnAdd = ui.findViewById(R.id.btn_add)
//        btnDeleteAll = ui.findViewById(R.id.btn_delete)
        dateRangeText = ui.findViewById(R.id.date_range)
        onDateSelectedSetText(viewModel.startDate.value!!, viewModel.endDate.value!!)

        viewModel.startDate.observe(viewLifecycleOwner) {
            onDateSelectedSetText(it, viewModel.endDate.value!!)
        }
        viewModel.endDate.observe(viewLifecycleOwner) {
            onDateSelectedSetText(viewModel.startDate.value!!, it)
        }

        viewModel.expensesHistory.observe(viewLifecycleOwner) {
            // Update Graph/Chart
            CoroutineScope(IO).launch {
                val categoryExpenseMap = mutableMapOf<Int, Int>()
                var totalExpense: Int = 0
                Log.i(TAG, it.size.toString())
                // Count and Add each expense category and its own total as well as total total
                it.forEach {
                    val expense = categoryExpenseMap[it.category]
                    if (expense != null) {
                        categoryExpenseMap[it.category] = expense + it.cost
                    } else {
                        categoryExpenseMap[it.category] = it.cost
                    }
                    totalExpense += it.cost
                }
                // Divide up the categories into slices
                val pieChartSlices = ArrayList<SliceValue>()
                categoryExpenseMap.forEach {
                    val categorySlice = SliceValue((it.value).toFloat(), ChartUtils.pickColor())
                    val categoryLabel = "Category #${it.key}: $${it.value}"
                    categorySlice.setLabel(categoryLabel)
                    pieChartSlices.add(categorySlice)
                }
                val pieData = PieChartData(pieChartSlices)
                pieData.setHasLabels(true)
                withContext(Main) {
                    pieChart.pieChartData = pieData

                }
            }
        }
//        btnAdd.setOnClickListener {
//            viewModel.addFakeEntries()
//        }
//        btnDeleteAll.setOnClickListener {
//            viewModel.deleteAll()
//        }
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

    // https://stackoverflow.com/questions/5645789/how-to-set-underline-text-on-textview
    // https://stackoverflow.com/questions/7953725/how-to-convert-milliseconds-to-date-format-in-android
    private fun onDateSelectedSetText(start: Long, end: Long) {
        val startDate = Instant.ofEpochMilli(start).atZone(ZoneOffset.UTC).toLocalDate()
        val endDate = Instant.ofEpochMilli(end).atZone(ZoneOffset.UTC).toLocalDate()
        val dateFormatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", Locale.CANADA)
        val startDateText = dateFormatter.format(startDate)
        val endDateText = dateFormatter.format(endDate)
        val underlineText = SpannableString("$startDateText - $endDateText")
        underlineText.setSpan(UnderlineSpan(), 0, underlineText.length, 0)
        dateRangeText.text = underlineText
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}