package com.example.wakewatch.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.HideReturnsTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.wakewatch.R
import com.example.wakewatch.MainActivity

class SignupActivity : AppCompatActivity() {
    // UI Components
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signupButton: AppCompatButton
    private lateinit var loginLink: TextView
    private lateinit var togglePasswordVisibility: ImageButton
    private lateinit var googleSignupButton: AppCompatButton
    private lateinit var facebookSignupButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize UI components
        initializeViews()

        // Set up click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        // Find all views by their IDs
        nameInput = findViewById(R.id.name_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        signupButton = findViewById(R.id.signup_button)
        loginLink = findViewById(R.id.login_link)
        togglePasswordVisibility = findViewById(R.id.toggle_password)
        googleSignupButton = findViewById(R.id.ic_google)
        facebookSignupButton = findViewById(R.id.facebook_signup)
    }

    private fun setupClickListeners() {
        // Signup Button
        signupButton.setOnClickListener {
            performSignup()
        }

        // Login Link
        loginLink.setOnClickListener {
            navigateToLogin()
        }

        // Password Visibility Toggle
        togglePasswordVisibility.setOnClickListener {
            togglePasswordVisibilityMethod()
        }

        // Social Signup Buttons
        googleSignupButton.setOnClickListener {
            performGoogleSignup()
        }

        facebookSignupButton.setOnClickListener {
            performFacebookSignup()
        }
    }

    private fun performSignup() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        // Validate inputs
        if (validateInputs(name, email, password)) {
            // Attempt signup
            if (registerUser(name, email, password)) {
                // Save user state
                saveUserState()

                // Navigate to main activity
                navigateToMainActivity()
            } else {
                showSignupError()
            }
        }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        return when {
            name.isEmpty() -> {
                nameInput.error = "Name cannot be empty"
                false
            }
            name.length < 2 -> {
                nameInput.error = "Name must be at least 2 characters"
                false
            }
            email.isEmpty() -> {
                emailInput.error = "Email cannot be empty"
                false
            }
            !isValidEmail(email) -> {
                emailInput.error = "Invalid email format"
                false
            }
            password.isEmpty() -> {
                passwordInput.error = "Password cannot be empty"
                false
            }
            password.length < 6 -> {
                passwordInput.error = "Password must be at least 6 characters"
                false
            }
            else -> true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        // Simple email validation regex
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun registerUser(name: String, email: String, password: String): Boolean {
        // TODO: Replace with actual user registration logic
        // This is a placeholder implementation
        return name.isNotEmpty() && email.isNotEmpty() && password.length >= 6
    }

    private fun saveUserState() {
        val sharedPrefs = getSharedPreferences("WakeWatchPrefs", MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putBoolean("is_logged_in", true)
            putBoolean("has_ever_logged_in", true)
            putString("user_name", nameInput.text.toString())
            apply()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close signup activity
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close signup activity
    }

    private fun togglePasswordVisibilityMethod() {
        if (passwordInput.transformationMethod is PasswordTransformationMethod) {
            // Show password
            passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
        } else {
            // Hide password
            passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
            togglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
        }
        // Move cursor to the end of the text
        passwordInput.setSelection(passwordInput.text.length)
    }

    private fun performGoogleSignup() {
        // TODO: Implement Google Sign-Up
        Toast.makeText(this, "Google Signup clicked", Toast.LENGTH_SHORT).show()
    }

    private fun performFacebookSignup() {
        // TODO: Implement Facebook Sign-Up
        Toast.makeText(this, "Facebook Signup clicked", Toast.LENGTH_SHORT).show()
    }

    private fun showSignupError() {
        Toast.makeText(this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show()
    }
}