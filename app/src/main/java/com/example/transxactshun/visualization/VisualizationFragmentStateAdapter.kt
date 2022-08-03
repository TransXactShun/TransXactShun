package com.example.transxactshun.visualization

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Adapter class for launching fragments from Main activity
 * @param activity - The activity that will launch the fragments
 * @param list - A list containing the fragments to be launched
 */
class VisualizationFragmentStateAdapter(activity: FragmentActivity, var list: ArrayList<Fragment>): FragmentStateAdapter(activity) {
    /**
     * Overridden getItemCount function
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Overridden createFragment function
     * Provide a new Fragment associated with the specified position.
     * @param position - the position in the Fragment list to create
     * @return the Fragment at specified position
     */
    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}