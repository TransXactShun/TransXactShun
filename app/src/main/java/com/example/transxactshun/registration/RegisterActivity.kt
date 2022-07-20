package com.example.transxactshun.registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.transxactshun.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    // firebase auth var
    private lateinit var auth: FirebaseAuth

    // vars for UI elements
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var phoneEditText: EditText

    // vars to store user information
    private var userName = ""
    private var userEmail = ""
    private var userPassword = ""
    private var userConfirmPassword = ""
    private var userPhone = ""

    // country code for phone number
    val COUNTRY_PREFIX = "+1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // retrieve UI elements
        nameEditText = findViewById(R.id.editTextRegisterName)
        emailEditText = findViewById(R.id.editTextRegisterEmail)
        passwordEditText = findViewById(R.id.editTextRegisterPassword)
        confirmPasswordEditText = findViewById(R.id.editTextRegisterConfirmPassword)
        phoneEditText = findViewById(R.id.editTextRegisterPhone)


    }

    fun onRegisterFirebase(view: View) {
        // retrieve data from editTextFields
        userName = nameEditText.text.toString()
        userEmail = emailEditText.text.toString()
        userPassword = passwordEditText.text.toString()
        userConfirmPassword = confirmPasswordEditText.text.toString()
        userPhone = phoneEditText.text.toString()

        // check if all fields have information
        if (userName.isNullOrBlank() || userEmail.isNullOrBlank() || userPassword.isNullOrBlank() || userConfirmPassword.isNullOrBlank() || userPhone.isNullOrBlank()) {
            // display error message that edit Text cannot be empty
            Toast.makeText(
                baseContext, "All fields are mandatory!.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // check password length
        if (userPassword.length < 6) {
            // display error message
            Toast.makeText(
                baseContext, "Password should have atleast 7 characters!.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // check password confirmation
        if (userPassword != userConfirmPassword) {
            // display error message
            Toast.makeText(
                baseContext, "Confirmation Password not correct!.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // check phone number format
        if (userPhone.length == 10) {
            userPhone = COUNTRY_PREFIX + userPhone
            if (!PhoneNumberUtils.isGlobalPhoneNumber(userPhone)) {
                // display error message if phone number is not in correct format
                Toast.makeText(
                    baseContext, "Please check your phone number!.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            // display error message if phone number is not in correct format
            Toast.makeText(
                baseContext, "Please check your phone number!.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }


        // if all checks pass, register user
        // Reference taken from: https://firebase.google.com/docs/auth/android/start
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // if registration succeeds
                    val user = auth.currentUser
                    //TODO: update user profile
                    // user.updateProfile()
                    Toast.makeText(
                        baseContext, "User registration complete!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // if registration fails, display message
                    Toast.makeText(
                        baseContext,
                        "User registration failed! Please check email, phone number, etc\n Or User already exists!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // close registration activity
                this.finish()
            }


    }


    fun onBackClick(view: View) {
        // close activity
        this.finish()
    }
}