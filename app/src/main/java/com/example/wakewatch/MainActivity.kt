package com.example.wakewatch

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up a simple TextView as the content
        val textView = TextView(this)
        textView.text = "WakeWatch App - Basic Setup"
        textView.textSize = 20f
        textView.setPadding(30, 30, 30, 30)

        // Set the TextView as our content
        setContentView(textView)

        // Basic Python check - won't crash if eye_detection.py is empty
        try {
            val python = Python.getInstance()
            // Just verify we can access Python, but don't try to call functions yet
            if (python != null) {
                textView.append("\n\nPython is initialized!")
            }
        } catch (e: Exception) {
            textView.append("\n\nPython initialization issue: ${e.message}")
        }
    }
}