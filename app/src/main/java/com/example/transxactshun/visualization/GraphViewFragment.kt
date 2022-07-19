package com.example.transxactshun.visualization

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.*
import com.example.transxactshun.R
import com.example.transxactshun.database.ExpensesDatabase
import com.example.transxactshun.database.ExpensesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.PieChartView

class GraphViewFragment(): Fragment() {
    private val TAG = "GraphViewFragment"
    private lateinit var appContext: Context
    private lateinit var pieChart: PieChartView
    private lateinit var btnDaily: Button
    private lateinit var btnWeekly: Button
    private lateinit var btnMonthly: Button

    // For test
    private lateinit var btnAdd: Button
    private lateinit var btnDeleteAll: Button

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
        btnAdd = ui.findViewById(R.id.btn_add)
        btnDeleteAll = ui.findViewById(R.id.btn_delete)
        btnDaily = ui.findViewById(R.id.btn_daily)
        btnWeekly = ui.findViewById(R.id.btn_weekly)
        btnMonthly = ui.findViewById(R.id.btn_monthly)
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
                    pieChartSlices.add(SliceValue((it.value).toFloat(), ChartUtils.pickColor()))
                }
                val pieData = PieChartData(pieChartSlices)
                withContext(Main) {
                    pieChart.pieChartData = pieData

                }
            }
        }
        btnAdd.setOnClickListener {
            viewModel.addFakeEntries()
        }
        btnDeleteAll.setOnClickListener {
            viewModel.deleteAll()
        }

        btnDaily.setOnClickListener {

        }
        btnWeekly.setOnClickListener {

        }
        btnMonthly.setOnClickListener {

        }

        return ui
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}