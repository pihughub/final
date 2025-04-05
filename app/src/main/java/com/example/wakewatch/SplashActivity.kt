package com.example.wakewatch

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.wakewatch.auth.LoginActivity
import com.example.wakewatch.auth.SignupActivity

class SplashActivity : AppCompatActivity() {
    // Splash screen duration in milliseconds
    private val SPLASH_DELAY: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Use Handler to delay navigation
        Handler(Looper.getMainLooper()).postDelayed({
            // Determine next screen based on authentication status
            navigateBasedOnAuthStatus()
        }, SPLASH_DELAY)
    }

    private fun navigateBasedOnAuthStatus() {
        val sharedPreferences = getSharedPreferences("WakeWatchPrefs", MODE_PRIVATE)

        when {
            // User is currently logged in
            sharedPreferences.getBoolean("is_logged_in", false) -> {
                navigateToDashboard()
            }

            // User has logged in before but not currently logged in
            sharedPreferences.getBoolean("has_ever_logged_in", false) -> {
                navigateToLogin()
            }

            // First-time user
            else -> {
                navigateToSignup()
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close splash activity
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close splash activity
    }

    private fun navigateToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish() // Close splash activity
    }
}