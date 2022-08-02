package com.example.transxactshun.registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.example.transxactshun.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    // UI elements
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var phoneEditText: EditText

    // User information

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Retrieve UI elements
        nameEditText = findViewById(R.id.editTextRegisterName)
        emailEditText = findViewById(R.id.editTextRegisterEmail)
        passwordEditText = findViewById(R.id.editTextRegisterPassword)
        confirmPasswordEditText = findViewById(R.id.editTextRegisterConfirmPassword)
        phoneEditText = findViewById(R.id.editTextRegisterPhone)
    }

    fun onRegisterFirebase(view: View) {
        //TODO: Implement Firebase call
    }

    fun onBackClick(view: View) {
        // Close activity
        this.finish()
    }
}