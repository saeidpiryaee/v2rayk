package com.KiowSoft.InsearchOfSnowy

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import java.lang.Boolean
import kotlin.Int






class MainActivity : AppCompatActivity() {

    private var currentScore = 0
    private lateinit var scoreTextView: TextView
    private val PREFS_NAME = "MyPrefs"
    private val SCORE_KEY = "score"
    val DEBUG = Boolean.parseBoolean("true")
    val APPLICATION_ID = "com.appodealstack.demo"
    val BUILD_TYPE = "debug"
    val VERSION_CODE = 1
    val VERSION_NAME = "1.0"

    // Field from default config.
    val APP_KEY = "8133866ba0105a2ce930ac403a35c074d95e8dcf2d33c12c"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Appodeal.initialize(this, "8133866ba0105a2ce930ac403a35c074d95e8dcf2d33c12c", Appodeal.REWARDED_VIDEO,
            object : ApdInitializationCallback {
                override fun onInitializationFinished(errors: List<ApdInitializationError>?) {

                }
            }
            )






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
        button1.setOnClickListener {

            if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                // Show the rewarded video ad
                Appodeal.show(this, Appodeal.REWARDED_VIDEO)
            } else {
                // Handle the case when the ad is not loaded
            }
        }
        button2.setOnClickListener {

            // Check if the rewarded video ad is loaded
            if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                // Show the rewarded video ad
                Appodeal.show(this, Appodeal.REWARDED_VIDEO)
            } else {
                // Handle the case when the ad is not loaded
            }
        }
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
