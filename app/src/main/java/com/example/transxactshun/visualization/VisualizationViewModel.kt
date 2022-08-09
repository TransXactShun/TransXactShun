package com.example.transxactshun.visualization

import android.util.Log
import androidx.lifecycle.*
import com.example.transxactshun.database.ExpenseCategory
import com.example.transxactshun.database.ExpensesDatabaseEntry
import com.example.transxactshun.database.ExpensesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.exp

data class TrendChartBuilderValue(val date: Long, val costs: IntArray, val totalCosts: Int, val summary: ArrayList<SingleExpenseSummary>)
data class SingleExpenseSummary(val date: Long, val vendor: String, val cost: Int, val category: ExpenseCategory)

class VisualizationViewModel(private val repository: ExpensesRepository) : ViewModel() {
    companion object {
        const val PACIFIC_TIME_OFFSET = 25200000L   // subtract this amount from milliseconds in UTC to get PST
        const val ONE_WEEK_IN_MS =    604800000L
        const val ONE_DAY_IN_MS =      86400000L
        fun closestDateFullWeekGoingBack(days: Int): Long {
            val now = LocalDateTime.now().atZone(ZoneOffset.UTC)
            var closestStartOfWeek = now
            when (now.dayOfWeek) {
                DayOfWeek.MONDAY -> closestStartOfWeek = now.minus(Period.ofDays(0))
                DayOfWeek.TUESDAY -> closestStartOfWeek = now.minus(Period.ofDays(1))
                DayOfWeek.WEDNESDAY -> closestStartOfWeek = now.minus(Period.ofDays(2))
                DayOfWeek.THURSDAY -> closestStartOfWeek = now.minus(Period.ofDays(3))
                DayOfWeek.FRIDAY -> closestStartOfWeek = now.minus(Period.ofDays(4))
                DayOfWeek.SATURDAY -> closestStartOfWeek = now.minus(Period.ofDays(5))
                DayOfWeek.SUNDAY -> closestStartOfWeek = now.minus(Period.ofDays(6))
                else -> {}
            }
            return closestStartOfWeek.minus(Period.ofDays(days)).toInstant().toEpochMilli()
        }
        fun isLeapYear(year: Int): Boolean {
            return year % 4 == 0 && year % 100 != 0
        }
    }
    val TAG = "VisualizationViewModel"
    val expensesHistory: LiveData<List<ExpensesDatabaseEntry>> = repository.entireExpensesHistory.asLiveData()
    val partialExpensesHistory: LiveData<List<ExpensesDatabaseEntry>> = //  One year worth of data sorted in order
        repository.getExpensesStartingFrom(Calendar.getInstance().timeInMillis - closestDateFullWeekGoingBack(365)).asLiveData()
    val startDate = MutableLiveData(Calendar.getInstance().timeInMillis - PACIFIC_TIME_OFFSET - ONE_WEEK_IN_MS)
    val endDate = MutableLiveData(Calendar.getInstance().timeInMillis - PACIFIC_TIME_OFFSET)
    val pieChartSelectedCategory = MutableLiveData<ExpenseCategory?>()
    val timeGroup = MutableLiveData<TimeGroup>(TimeGroup.WEEKLY)
    val totalCostInTimeGroup = MutableLiveData<Int>(0)

    /**
     * Retrieves expense history of 1 category between two dates
     * @param start - the start date in milliseconds
     * @param end - the end date in milliseconds
     * @param category - the category as an enum class (ExpenseCategory), if null then
     *                   ignore categories and get all expenses between the two provided dates
     * @return the expenses in an ArrayList of SingleExpenseSummary objects
     */
    fun getSingleExpenseCategorySummaryBetween(start: Long, end: Long, category: ExpenseCategory? = null): ArrayList<SingleExpenseSummary> {
        val expenseSummary = ArrayList<SingleExpenseSummary>()
        expensesHistory.value?.forEach {
            val entryCategory = ExpenseCategory.values()[it.category]
            if (it.epochDate in start..end) {
                if (category == null) {
                    expenseSummary.add(SingleExpenseSummary(it.epochDate, it.vendor, it.cost, entryCategory))
                }
                else {
                    if (category.ordinal == it.category) {
                        expenseSummary.add(SingleExpenseSummary(it.epochDate, it.vendor, it.cost, entryCategory))
                    }
                }
            }
        }
        return expenseSummary
    }

    /**
     * Retrieves all expenses between two dates separated by category
     * @param start - the start date in milliseconds
     * @param end - the end date in milliseconds
     * @return a triple tuple as follows:
     * - first is a map of ExpenseCategory, int where the first is the category (based on enum class ExpenseCategory)
     *      and the second is the total expense of that category
     * - second is the total expense of every category
     * - third is an arraylist containing all the expenses as SingleExpenseSummary objects
     */
    fun getExpensesByCategoryBetween(start: Long, end: Long): Triple<Map<ExpenseCategory, Int>, Int, ArrayList<SingleExpenseSummary>> {
        val expenseSummary = ArrayList<SingleExpenseSummary>()
        val categoryExpenseMap = mutableMapOf<ExpenseCategory, Int>()
        var totalExpense: Int = 0

        // Count and Add each expense category and its own total as well as total total
        expensesHistory.value?.forEach {
            if (it.epochDate in start..end) {
                val category = ExpenseCategory.values()[it.category]
                val expense = categoryExpenseMap[category]
                if (expense != null) {
                    categoryExpenseMap[category] = expense + it.cost
                } else {
                    categoryExpenseMap[category] = it.cost
                }
                totalExpense += it.cost
                if (pieChartSelectedCategory.value == null) {
                    expenseSummary.add(SingleExpenseSummary(it.epochDate, it.vendor, it.cost, category))
                }
                else {
                    if (pieChartSelectedCategory.value!!.ordinal == it.category) {
                        expenseSummary.add(SingleExpenseSummary(it.epochDate, it.vendor, it.cost, category))
                    }
                }
            }
        }
        return Triple(categoryExpenseMap, totalExpense, expenseSummary)
    }

    /**
     * Filters and separates transactions based on a TimeGroup (daily, weekly, monthly) and returns
     * the results with an array of data class objects
     * @param timeGroup - A TimeGroup enum that specifies the temporal category to group transactions in
     * @return a TrendChartBuilderValue data object containing the following:
     *      1. The start of the time group in milliseconds (ex. the start of a week)
     *      2. An array for each category cost (index is equivalent to the ordinal value of categories in ExpenseCategory enum class)
     *      3. An array containing certain transaction information for every expense tallied
     */
    // https://stackoverflow.com/questions/23944370/how-to-get-milliseconds-from-localdatetime-in-java-8
    suspend fun getExpensesByTimeGroup(timeGroup: TimeGroup): ArrayList<TrendChartBuilderValue> {
        var amountOfData = 0    // Will be in days/weeks/months depending on user selection
        var amountOfDataInDays = 0  // For determining how many days to go back
        lateinit var periodInMilliseconds: LongArray
        when (timeGroup) {
            // Have to allow some extra leeway because when weeks is selected, we need to ensure that
            // full week is selected starting from Monday, therefore the partialExpensesHistory
            // can contain a year (plus up to 7 days) of data
            TimeGroup.DAILY -> {
                amountOfData = 68
                amountOfDataInDays = 60
                periodInMilliseconds = LongArray(amountOfData) { ONE_DAY_IN_MS }
            }
            TimeGroup.WEEKLY -> {
                amountOfData = 54
                amountOfDataInDays = 365
                periodInMilliseconds = LongArray(amountOfData) { ONE_WEEK_IN_MS }
            }
        }
        val results = ArrayList<TrendChartBuilderValue>(amountOfData)
        val timePeriod = LongArray(amountOfData) {0L}
        val totalInPeriodPerCategory = Array(amountOfData) {IntArray(ExpenseCategory.values().size) {0} }
        val totalInPeriod = IntArray(amountOfData) {0}
        val expensesInPeriod = Array<ArrayList<SingleExpenseSummary>>(amountOfData) {ArrayList()}
        val now = Calendar.getInstance().timeInMillis
        var startOfPeriod = closestDateFullWeekGoingBack(amountOfDataInDays)

        // Loop 1 establishes the time bounds for each week
        var index = 0
        while (index < amountOfData) {
            timePeriod[index] = startOfPeriod
            startOfPeriod += periodInMilliseconds[index]
            index += 1
        }

        // Loop 2 finds all expenses in each time bound and adds them to total and expenses
        partialExpensesHistory.value?.forEach {
            val i = ((timePeriod.binarySearch(it.epochDate)) * -1) - 1
            if (i in 0 until amountOfData) {
                totalInPeriodPerCategory[i][it.category] += it.cost
                totalInPeriod[i] += it.cost
                expensesInPeriod[i].add(
                    SingleExpenseSummary(
                        it.epochDate,
                        it.vendor,
                        it.cost,
                        ExpenseCategory.values()[it.category]
                    )
                )
            }
        }

        timePeriod.forEachIndexed { index, period ->
            if (period in 1..now) {
                results.add(TrendChartBuilderValue(period, totalInPeriodPerCategory[index], totalInPeriod[index], expensesInPeriod[index]))
            }
        }
        return results
    }
}

class VisualizationViewModelFactory(private val repository: ExpensesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisualizationViewModel::class.java))
            return VisualizationViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}