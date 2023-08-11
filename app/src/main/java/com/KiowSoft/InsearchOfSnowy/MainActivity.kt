package com.KiowSoft.InsearchOfSnowy

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.RewardedVideoCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var currentScore = 0
    private lateinit var scoreTextView: TextView
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var centerButton: Button
    private val PREFS_NAME = "MyPrefs"
    private val SCORE_KEY = "score"
    private lateinit var  loadingTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    //private var htmlString = File("http://v2rayiran.top/v2links/1day.html").readText()


    private val adCheckIntervalMillis: Long = 3000 // Check every 3 seconds
    private val handler = Handler(Looper.getMainLooper())
    private val adCheckRunnable = object : Runnable {
        override fun run() {
            checkAdLoadedStatus()
            handler.postDelayed(this, adCheckIntervalMillis)
        }
    }

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
        loadingTextView = findViewById(R.id.loadingTextView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)


        // Restore the saved score
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        currentScore = prefs.getInt(SCORE_KEY, 0)
        updateScore()

        button1.setOnClickListener {

        }

        centerButton.setOnClickListener {
            if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                // Show the rewarded video ad
                Appodeal.show(this, Appodeal.REWARDED_VIDEO)
            } else {
                // Handle the case when the ad is not loaded
            }
        }

        // Implement button2 and other button click listeners here

        checkAdLoadedStatus()
        handler.post(adCheckRunnable)

        Appodeal.setRewardedVideoCallbacks(object : RewardedVideoCallbacks {

            override fun onRewardedVideoLoaded(isPrecache: Boolean) {
                //showToast("Rewarded video was loaded, isPrecache: $isPrecache")
            }

            override fun onRewardedVideoFailedToLoad() {
                //showToast("Rewarded video failed to load")
            }

            override fun onRewardedVideoClicked() {
                //showToast("Rewarded video was clicked")
            }

            override fun onRewardedVideoShowFailed() {
                //showToast("Rewarded video failed to show")
            }

            override fun onRewardedVideoShown() {
                //showToast("Rewarded video was shown")
            }

            override fun onRewardedVideoClosed(finished: Boolean) {
              //  showToast("Rewarded video was closed, isVideoFinished: $finished")
            }

            override fun onRewardedVideoFinished(amount: Double, name: String?) {
               // showToast("Rewarded video was finished, amount: $amount, currency: $name")
                currentScore++
                updateScore()
            }

            override fun onRewardedVideoExpired() {
                //showToast("Rewarded video was expired")
            }
        })

        val url = "http://v2rayiran.top/v2links/1day.html"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val htmlString = fetchHtmlContent(url)

                // Switch to the main thread to display the Toast
                launch(Dispatchers.Main) {
                    showToast(htmlString)
                }

            } catch (e: IOException) {
                launch(Dispatchers.Main) {
                    showToast("Error fetching content")
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        // Save the score when the app is closed
        val editor: SharedPreferences.Editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        editor.putInt(SCORE_KEY, currentScore)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove callbacks when the activity is destroyed
        handler.removeCallbacks(adCheckRunnable)
    }

    private fun updateScore() {
        scoreTextView.text = "Score: $currentScore"
    }

    private fun checkAdLoadedStatus() {
        val isAdLoaded = Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)

        if (isAdLoaded) {
            // Ad is loaded, update UI or perform actions
            centerButton.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.INVISIBLE
            loadingTextView.visibility = View.INVISIBLE
        } else {
            // Ad is not loaded, update UI or perform actions
             centerButton.visibility = View.INVISIBLE
             loadingProgressBar.visibility = View.VISIBLE
             loadingTextView.visibility = View.VISIBLE

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private suspend fun fetchHtmlContent(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

    // Implement other methods here
}
