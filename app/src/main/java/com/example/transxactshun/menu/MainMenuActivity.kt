package com.example.transxactshun.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.transxactshun.MainActivity
import com.example.transxactshun.R
import com.example.transxactshun.visualization.VisualizationActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainMenuActivity : AppCompatActivity() {
    private lateinit var btnVisualization: Button

    // store current user email
    private lateinit var userEmail: String
    private lateinit var userEmailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // retrieve text view from UI
        userEmailTextView = findViewById(R.id.textViewUserEmail)
        // retrieve current user email
        val user = Firebase.auth.currentUser
        if (user != null) {
            userEmail = user.email.toString()

            // update text view
            userEmailTextView.text = "Email: $userEmail"
        } else {
            // return to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        // menu options
        btnVisualization = findViewById(R.id.btn_visualization)
        btnVisualization.setOnClickListener {
            val intent = Intent(applicationContext, VisualizationActivity::class.java).apply {
                // nothing for now
            }
            startActivity(intent)
        }
    }
}