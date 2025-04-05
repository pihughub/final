package com.example.wakewatch.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakewatch.MainActivity
import com.example.wakewatch.R

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_sign)

        // Initialize UI components
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)

        // Set up login button click listener
        loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulate login (replace with actual authentication logic)
        if (validateCredentials(email, password)) {
            // Save login state
            val sharedPrefs = getSharedPreferences("WakeWatchPrefs", MODE_PRIVATE)
            sharedPrefs.edit().apply {
                putBoolean("is_logged_in", true)
                putBoolean("has_ever_logged_in", true)
                apply()
            }

            // Navigate to Main Activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        // Placeholder authentication logic
        // TODO: Replace with actual authentication mechanism
        return email.isNotEmpty() && password.length >= 6
    }
}