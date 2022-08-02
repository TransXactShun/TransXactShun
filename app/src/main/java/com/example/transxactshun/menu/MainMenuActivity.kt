package com.example.transxactshun.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.transxactshun.R
import com.example.transxactshun.transactions.TransactionHistoryActivity

//import com.example.transxactshun.visualization.VisualizationActivity

class MainMenuActivity : AppCompatActivity() {
    private lateinit var btnVisualization: Button
    private lateinit var btnTransactions: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        btnTransactions = findViewById(R.id.btn_transactions)
        btnTransactions.setOnClickListener {
            val intent = Intent(applicationContext, TransactionHistoryActivity::class.java).apply {
                // nothing for now
            }
            startActivity(intent)
        }
        btnTransactions = findViewById(R.id.btn_transactions)
//        btnVisualization.setOnClickListener {
//            val intent = Intent(applicationContext, VisualizationActivity::class.java).apply {
//                // nothing for now
//            }
//            startActivity(intent)
//        }
    }
}