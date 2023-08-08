package com.KiowSoft.InsearchOfSnowy

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var currentScore = 0
    private lateinit var scoreTextView: TextView
    private val PREFS_NAME = "MyPrefs"
    private val SCORE_KEY = "score"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreTextView = findViewById(R.id.scoreTextView)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)
        val centerButton: Button = findViewById(R.id.centerButton)

        // Restore the saved score
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        currentScore = prefs.getInt(SCORE_KEY, 0)
        updateScore()

        // Set click listeners for buttons (you can add functionality to these buttons if needed)
        button1.setOnClickListener { }
        button2.setOnClickListener { }
        button3.setOnClickListener { }
        centerButton.setOnClickListener { }
    }

    override fun onStop() {
        super.onStop()
        // Save the score when the app is closed
        val editor: SharedPreferences.Editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        editor.putInt(SCORE_KEY, currentScore)
        editor.apply()
    }

    private fun updateScore() {
        scoreTextView.text = "Score: $currentScore"
    }

    // You can call this method whenever you want to increase the score
    private fun increaseScore(points: Int) {
        currentScore += points
        updateScore()
    }
}
