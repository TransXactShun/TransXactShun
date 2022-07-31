package com.example.transxactshun.visualization

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.transxactshun.R
import com.example.transxactshun.database.ExpenseCategory
import com.example.transxactshun.database.ExpensesDatabase
import com.example.transxactshun.database.ExpensesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import lecho.lib.hellocharts.formatter.AxisValueFormatter
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ViewportChangeListener
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.ColumnChartView
import lecho.lib.hellocharts.view.PreviewColumnChartView

enum class TimeGroup {
    DAILY,
    WEEKLY,
    MONTHLY
}

class TrendViewFragment: Fragment() {
    private val TAG = "TrendViewFragment"
    private lateinit var appContext: Context
    private lateinit var mainChart: ColumnChartView
    private lateinit var previewChart: PreviewColumnChartView
    private lateinit var btnDaily: Button
    private lateinit var btnWeekly: Button
    private lateinit var btnMonthly: Button
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
        btnDaily = ui.findViewById(R.id.btn_daily)
        btnWeekly = ui.findViewById(R.id.btn_weekly)
        btnMonthly = ui.findViewById(R.id.btn_monthly)

        mainChart.isZoomEnabled = false
        mainChart.isScrollEnabled = false
        previewChart.setViewportChangeListener(ViewportListener())

        btnDaily.setOnClickListener {
            viewModel.timeGroup.value = TimeGroup.DAILY
        }
        btnWeekly.setOnClickListener {
            viewModel.timeGroup.value = TimeGroup.WEEKLY
        }
        btnMonthly.setOnClickListener {
            viewModel.timeGroup.value = TimeGroup.MONTHLY
        }

        viewModel.partialExpensesHistory.observe(viewLifecycleOwner) {
            buildColumnCharts(viewModel.timeGroup.value!!)
        }

        viewModel.timeGroup.observe(viewLifecycleOwner) {
            buildColumnCharts(it)
        }
        return ui
    }

    /**
     * Builds the main and preview charts
     * @param timeGroup - enum class TimeGroup for daily, weekly or monthly view
     */
    private fun buildColumnCharts(timeGroup: TimeGroup) {
        lateinit var chartBuilderValue: TrendChartBuilderValue
        when (timeGroup) {
            TimeGroup.DAILY -> {chartBuilderValue = viewModel.getExpensesByTimeGroup(TimeGroup.DAILY)}
            TimeGroup.WEEKLY -> {chartBuilderValue = viewModel.getExpensesByTimeGroup(TimeGroup.WEEKLY)}
            TimeGroup.MONTHLY -> {chartBuilderValue= viewModel.getExpensesByTimeGroup(TimeGroup.MONTHLY)}
            else -> {}
        }
        val numColumns = chartBuilderValue.dates.size
        val mainColumnValues = ArrayList<Column>()
        val previewColumnValues = ArrayList<Column>()
//        val expenseCategories = ExpenseCategory.values()
        for (period in 0 until numColumns) {
            val subcolumnValues = ArrayList<SubcolumnValue>()
            var costInPeriod = 0
            chartBuilderValue.costs[period].forEachIndexed { index, cost ->
                costInPeriod += cost / 100
//                subcolumnValues.add(SubcolumnValue(cost.toFloat(), getCategoryColour(expenseCategories[index])))
            }
            subcolumnValues.add(SubcolumnValue(costInPeriod.toFloat(), ChartUtils.pickColor()))
            mainColumnValues.add(Column(subcolumnValues))
            previewColumnValues.add(Column(subcolumnValues))
        }
        val data = ColumnChartData(mainColumnValues)
        val previewData = ColumnChartData(previewColumnValues)
//        val formattedDatesLabels = ArrayList<String>()
        val formattedDatesLabels = ArrayList<AxisValue>()
        chartBuilderValue.dates.forEachIndexed { index, millisecond ->
            // Approximately 5 date labels should be visible
            if (index % (numColumns/5) == 0)
                formattedDatesLabels.add(AxisValue(index.toFloat()).setLabel(VisualizationUtil.millisecondsToDateFormat(millisecond)))
        }
        data.axisXBottom = Axis().setAutoGenerated(false).setValues(formattedDatesLabels)
        data.axisYLeft = Axis().setHasLines(true)
        mainChart.columnChartData = data
        previewChart.columnChartData = previewData
        previewX(true)
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