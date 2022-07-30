package com.example.transxactshun.menu

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.transxactshun.R

class MenuAdapter(
    var activity: FragmentActivity?,
    var itemlist: ArrayList<String>,
    var imageList: ArrayList<Int>
) : BaseAdapter() {
    override fun getCount(): Int {
        return itemlist.size
    }

    override fun getItem(position: Int): Any {
        return itemlist[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = View.inflate(activity, R.layout.menu_adapter, null)
        //image
        var menuImageView: ImageView = view.findViewById(R.id.imageViewMenuImage)
        menuImageView.setImageResource(imageList[position])

        // textview
        var menuTextView: TextView = view.findViewById(R.id.textViewMenuOption)
        menuTextView.text = itemlist[position]


        return view
    }
}