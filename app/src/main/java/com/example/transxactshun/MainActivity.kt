package com.example.transxactshun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.transxactshun.menu.MainMenuActivity
import com.example.transxactshun.registration.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // firebase auth var
    private lateinit var auth: FirebaseAuth

    // UI element vars
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // retrieve UI elements
        emailEditText = findViewById(R.id.editTextUserEmail)
        passwordEditText = findViewById(R.id.editTextUserPassword)
    }

    fun onRegisterClick(view: View) {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }

    fun onLoginClick(view: View) {
        openMainMenu() // TODO: Note from Jason. Uncomment this line and comment out code below to skip login

        var email = emailEditText.text.toString()
        var password = passwordEditText.text.toString()
        // Firebase login procedure
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            // display error message that edit Text cannot be empty
            Toast.makeText(
                baseContext, "Email and Password cannot be empty!.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // continue login
            // Reference taken from: https://firebase.google.com/docs/auth/android/start


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // if user is authenticated, open main menu
                        val user = auth.currentUser
                        openMainMenu()
                    } else {
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun openMainMenu() {
        val mainMenuIntent = Intent(this, MainMenuActivity::class.java)
        startActivity(mainMenuIntent)
        finish()
    }
}