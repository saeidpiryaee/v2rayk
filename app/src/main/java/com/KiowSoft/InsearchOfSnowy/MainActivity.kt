package com.KiowSoft.InsearchOfSnowy

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError

class MainActivity : AppCompatActivity() {

    private var currentScore = 0
    private lateinit var scoreTextView: TextView
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var centerButton: Button
    private val PREFS_NAME = "MyPrefs"
    private val SCORE_KEY = "score"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Appodeal.initialize(this, "8133866ba0105a2ce930ac403a35c074d95e8dcf2d33c12c", Appodeal.REWARDED_VIDEO,
            object : ApdInitializationCallback {
                override fun onInitializationFinished(errors: List<ApdInitializationError>?) {
                    // Handle initialization finished
                }
            })

        scoreTextView = findViewById(R.id.scoreTextView)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        centerButton = findViewById(R.id.centerButton)

        // Restore the saved score
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        currentScore = prefs.getInt(SCORE_KEY, 0)
        updateScore()

        button1.setOnClickListener {
            if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                // Show the rewarded video ad
                Appodeal.show(this, Appodeal.REWARDED_VIDEO)
            } else {
                // Handle the case when the ad is not loaded
            }
        }
        button2.setOnClickListener {
            if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                // Show the rewarded video ad
                Appodeal.show(this, Appodeal.REWARDED_VIDEO)
            } else {
                // Handle the case when the ad is not loaded
            }
        }
        button3.setOnClickListener { /* Handle button3 click */ }
        centerButton.setOnClickListener { /* Handle centerButton click */ }


        checkAdLoadedStatus()
        //button1.visibility = View.INVISIBLE
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

    private fun checkAdLoadedStatus() {
        val isAdLoaded = Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)

        if (isAdLoaded) {
            // Ad is loaded, update UI or perform actions
             button1.visibility = View.VISIBLE
        } else {
            // Ad is not loaded, update UI or perform actions
           // button1.visibility = View.INVISIBLE
        }

        // Schedule the next check (e.g., after a delay or using a handler)
        // You can use a handler, coroutine, or other mechanisms for scheduling checks.
    }


    // You can call this method whenever you want to increase the score
    private fun increaseScore(points: Int) {
        currentScore += points
        updateScore()
    }
}
