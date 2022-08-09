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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.transxactshun.R
import com.example.transxactshun.database.ExpenseCategory
import com.example.transxactshun.database.ExpensesDatabase
import com.example.transxactshun.database.ExpensesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lecho.lib.hellocharts.formatter.AxisValueFormatter
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ViewportChangeListener
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.ColumnChartView
import lecho.lib.hellocharts.view.PreviewColumnChartView

enum class TimeGroup(val displayValue: String) {
    DAILY("Daily"),
    WEEKLY("Weekly")
}

class TrendViewFragment: Fragment() {
    private val TAG = "TrendViewFragment"
    private lateinit var appContext: Context
    private lateinit var mainChart: ColumnChartView
    private lateinit var previewChart: PreviewColumnChartView
    private lateinit var btnDaily: Button
    private lateinit var btnWeekly: Button
//    private lateinit var btnMonthly: Button
    private lateinit var currentCategoryText: TextView
    private lateinit var totalCostTextValue: TextView
    private lateinit var totalCostTextHeader: TextView
    private lateinit var costReportView: RecyclerView
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
        val ui = inflater.inflate(R.layout.visualization_column_trend_layout, container, false)
        mainChart = ui.findViewById(R.id.main_column_chart)
        previewChart = ui.findViewById(R.id.preview_column_chart)
        currentCategoryText = ui.findViewById(R.id.current_time_group_text)
        btnDaily = ui.findViewById(R.id.btn_daily)
        btnWeekly = ui.findViewById(R.id.btn_weekly)
        costReportView = ui.findViewById(R.id.trend_report_summary)
        totalCostTextHeader = ui.findViewById(R.id.trend_report_header)
        totalCostTextValue = ui.findViewById(R.id.trend_report_total)

        mainChart.isZoomEnabled = false
        mainChart.isScrollEnabled = false
        previewChart.setViewportChangeListener(ViewportListener())

        btnDaily.setOnClickListener {
            viewModel.timeGroup.value = TimeGroup.DAILY
        }
        btnWeekly.setOnClickListener {
            viewModel.timeGroup.value = TimeGroup.WEEKLY
        }
//        btnMonthly.setOnClickListener {
//            viewModel.timeGroup.value = TimeGroup.MONTHLY
//        }

        viewModel.partialExpensesHistory.observe(viewLifecycleOwner) {
            buildColumnCharts(viewModel.timeGroup.value!!)
        }

        viewModel.timeGroup.observe(viewLifecycleOwner) {
            buildColumnCharts(it)
            val displayText = "${it.displayValue} Trend"
            val underlineText = SpannableString(displayText)
            underlineText.setSpan(UnderlineSpan(), 0, underlineText.length, 0)
            currentCategoryText.text = underlineText
        }

        viewModel.totalCostInTimeGroup.observe(viewLifecycleOwner) {
            val displayText = VisualizationUtil.currencyFormat(it)
            val underlineText = SpannableString(displayText)
            underlineText.setSpan(UnderlineSpan(), 0, underlineText.length, 0)
            totalCostTextValue.text = underlineText
        }
        return ui
    }

    /**
     * Builds the main and preview charts
     * @param timeGroup - enum class TimeGroup for daily, weekly or monthly view
     */
    private fun buildColumnCharts(timeGroup: TimeGroup) {
        CoroutineScope(IO).launch {
            lateinit var chartBuilderValue: ArrayList<TrendChartBuilderValue>
            when (timeGroup) {
                TimeGroup.DAILY -> {chartBuilderValue = viewModel.getExpensesByTimeGroup(TimeGroup.DAILY)}
                TimeGroup.WEEKLY -> {chartBuilderValue = viewModel.getExpensesByTimeGroup(TimeGroup.WEEKLY)}
                else -> {}
            }
            val numColumns = chartBuilderValue.size

            // Update the Total Cost
            CoroutineScope(IO).launch {
                val total = chartBuilderValue.sumOf {
                    it.totalCosts
                }
                withContext(Main) {
                    viewModel.totalCostInTimeGroup.value = total
                }
            }

            // Update the RecyclerView
            CoroutineScope(IO).launch {
                val sortedExpenses = chartBuilderValue.sortedBy {
                    it.totalCosts
                }.reversed()
                val reportAdapter = SingleTotalReportGridAdapter(sortedExpenses) {
                        position -> Log.i(TAG, "$position selected")
                }
                withContext(Main) {
                    costReportView.adapter = reportAdapter
                }
            }

            // Update the Chart
            CoroutineScope(IO).launch {
                val mainColumnValues = ArrayList<Column>()
                val previewColumnValues = ArrayList<Column>()
                for (period in 0 until numColumns) {
                    val subcolumnValues = ArrayList<SubcolumnValue>()
                    var costInPeriod = 0
    //                chartBuilderValue[period].costs.forEachIndexed { index, cost ->
    //                    costInPeriod += cost/100
    //                    total += cost
    //                    Log.i(TAG, "CostInPeriod: $costInPeriod")
    //                }
                    costInPeriod += chartBuilderValue[period].totalCosts / 100
                    val columnColour = ChartUtils.pickColor()
                    val subColumn = SubcolumnValue(costInPeriod.toFloat(), columnColour)
                    if (costInPeriod > 0) {
                        val label = VisualizationUtil.currencyFormat(costInPeriod*100)
                        subColumn.setLabel(label)
                    }
                    subcolumnValues.add(SubcolumnValue(subColumn))
                    val mainColumn = Column(subcolumnValues)
                    val previewColumn = Column(subcolumnValues)
                    if (costInPeriod > 0) mainColumn.setHasLabels(true)
                    mainColumnValues.add(mainColumn)
                    previewColumnValues.add(previewColumn)
                }
                val data = ColumnChartData(mainColumnValues)
                val previewData = ColumnChartData(previewColumnValues)
                val formattedDatesLabels = ArrayList<AxisValue>()
                chartBuilderValue.forEachIndexed { index, values ->
                    // Approximately 5 date labels should be visible
                    if (index % (numColumns/5) == 0)
                        formattedDatesLabels.add(AxisValue(index.toFloat()).setLabel(VisualizationUtil.millisecondsToDateFormat(values.date)))
                }
                withContext(Main) {
                    data.axisXBottom = Axis().setAutoGenerated(false).setValues(formattedDatesLabels)
                    data.axisYLeft = Axis().setHasLines(true)
                    mainChart.columnChartData = data
                    previewChart.columnChartData = previewData
                    previewX(true)
                }
            }
        }
    }

    /**
     * Builds the data for the report summary and creates the adapter for the recycler view
     * @param expenseSummary - an arraylist of SingleExpenseSummary objects
     */
    private suspend fun buildReportSummary(expenses: List<TrendChartBuilderValue>) {

    }

    /**
     * Function to return a specific colour for a specific ExpenseCategory
     * Is the same as in ChartViewFragment
     */
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

    /**
     * The following code is adapted from the HelloCharts Android library samples
     * https://github.com/lecho/hellocharts-android/blob/master/hellocharts-samples/src/lecho/lib/hellocharts/samples/PreviewColumnChartActivity.java
     */
    private fun previewY() {
        val tempViewport = Viewport(mainChart.maximumViewport)
        val dy = tempViewport.height() / 4
        tempViewport.inset(0f, dy)
        previewChart.setCurrentViewportWithAnimation(tempViewport)
        previewChart.zoomType = ZoomType.VERTICAL
    }

    private fun previewX(animate: Boolean) {
        val tempViewport = Viewport(mainChart.maximumViewport)
        val dx = tempViewport.width() / 4
        tempViewport.inset(dx, 0f)
        if (animate) {
            previewChart.setCurrentViewportWithAnimation(tempViewport)
        } else {
            previewChart.currentViewport = tempViewport
        }
        previewChart.zoomType = ZoomType.HORIZONTAL
    }

    private fun previewXY() {
        // Better to not modify viewport of any chart directly so create a copy.
        val tempViewport = Viewport(mainChart.maximumViewport)
        // Make temp viewport smaller.
        val dx = tempViewport.width() / 4
        val dy = tempViewport.height() / 4
        tempViewport.inset(dx, dy)
        previewChart.setCurrentViewportWithAnimation(tempViewport)
    }

    inner class ViewportListener: ViewportChangeListener {
        /**
         * Called when current viewport of chart changed. You should not modify that viewport.
         */
        override fun onViewportChanged(viewport: Viewport?) {
            mainChart.currentViewport = viewport
        }

    }
}