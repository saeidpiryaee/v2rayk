package com.KiowSoft.InsearchOfSnowy

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.RewardedVideoCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private var jso = ""
    private var p1day = ""
    private var p3day = ""
    private var p7day = ""
    private var currentScore = 0
    private lateinit var textBox: EditText
    private lateinit var copyButton: Button
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

    private val serverUrl = "http://vapp.v2rayiran.tech:5000/get_data"  // Replace with your server's URL
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
        textBox = findViewById(R.id.textBox)
        copyButton = findViewById(R.id.copyButton)
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
        fetchDataFromServer()
       // fetchDataFromServer1()


       // val url = "http://v2rayiran.top/v2links/1day.html"

//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val htmlString = fetchHtmlContent(url)
//
//                // Switch to the main thread to display the Toast
//                launch(Dispatchers.Main) {
//                    showToast(htmlString)
//                }
//
//            } catch (e: IOException) {
//                launch(Dispatchers.Main) {
//                    showToast("Error fetching content")
//                }
//            }
//        }





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



    private fun fetchDataFromServer() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(serverUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    showToast("Failed to fetch data from the server")

                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    try {
                        val jsonData = JSONObject(it)
                        runOnUiThread {
                            // Handle the JSON data here
                            showToast(jsonData.toString())
                            textBox.setText(jsonData.toString())
                            p1day = jsonData.getInt("p1day").toString()
                            p3day = jsonData.getInt("p3day").toString()
                            p7day = jsonData.getInt("p7day").toString()

                            showToast(p1day)
                            textBox.setText(p1day)
                            button1.text = "1 rooze " + "-" + p1day
                            button2.text = "3 rooze " + "-" + p3day
                            button3.text = "7 rooze " + "-" + p7day
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            showToast("Error parsing JSON response")
                        }
                    }
                }
            }
        })
    }


    private fun fetchDataFromServer1() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(serverUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    showToast("Failed to fetch data from the server")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    try {
                        val jsonData = JSONObject(it)

                        // Extract the text from the JSON data
                        val textFromJson = jsonData.getString("text_key")

                        // Set the extracted text to the EditText
                        runOnUiThread {
                            textBox.setText(textFromJson)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            showToast("Error parsing JSON response")
                        }
                    }
                }
            }
        })
    }

     fun getDataFromServer(callback: (JSONObject?) -> Unit) {
        val client = OkHttpClient()
        val url = "http://3.68.229.201:5000/get_data"  // Replace with your server's URL

        val request = Request.Builder()
            .url(url)
            .build()

        client.run {

            newCall(request).enqueue(object : Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    // Handle network failure
                    e.printStackTrace()
                    callback(null)
                }

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    if (!response.isSuccessful) {
                        // Handle non-successful response
                        callback(null)
                        return
                    }

                    val responseBody = response.body?.string()
                    responseBody?.let {
                        try {
                            // Parse the JSON response
                            val jsonData = JSONObject(it)
                            callback(jsonData)
                        } catch (e: Exception) {
                            // Handle JSON parsing error
                            e.printStackTrace()
                            callback(null)
                        }
                    }
                }
            })
        }
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
