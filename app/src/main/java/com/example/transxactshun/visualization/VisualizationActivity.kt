package com.example.transxactshun.visualization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.transxactshun.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

enum class VisualizationCalendarViews(val string: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

enum class VisualizationGraphViews(val string: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

class VisualizationActivity: AppCompatActivity() {
    private val TAG = "VisualizationActivity"
    private lateinit var graphFragment: GraphViewFragment
    private lateinit var calendarFragment: CalendarViewFragment
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var tab: TabLayout
    private lateinit var viewPage: ViewPager2
    private lateinit var visualFragmentStateAdapter: FragmentStateAdapter
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.visualization_main_layout)
        tab = findViewById(R.id.tab_layout)
        viewPage = findViewById(R.id.tab_view)
        fragments = ArrayList()

        val graphFragmentTag = "graphFragment"
        if (supportFragmentManager.findFragmentByTag(graphFragmentTag) == null) {
            graphFragment = GraphViewFragment()
            fragments.add(graphFragment)
        }

        val calendarFragmentTag = "calendarFragment"
        if (supportFragmentManager.findFragmentByTag(calendarFragmentTag) == null) {
            calendarFragment = CalendarViewFragment()
            fragments.add(calendarFragment)
        }

        visualFragmentStateAdapter = VisualizationFragmentStateAdapter(this, fragments)
        viewPage.adapter = visualFragmentStateAdapter
        val labels = arrayOf("Graph View", "Calendar View")
        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy() {
            tab: TabLayout.Tab, position: Int ->
                tab.text = labels[position]
        }
        tabLayoutMediator = TabLayoutMediator(tab, viewPage, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}