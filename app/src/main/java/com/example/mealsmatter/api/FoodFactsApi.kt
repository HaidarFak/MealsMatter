package com.example.mealsmatter.api

import com.example.mealsmatter.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.HttpURLConnection
import java.io.IOException
import android.util.Log

// Singleton-style object for static-like functions
class FoodFactsApi {
    companion object {
        // Used for debugging/logging
        private const val TAG = "FoodFactsApi"
        // Endpoint URL for Spoonacular's food trivia
        private const val BASE_URL = "https://api.spoonacular.com/food/trivia/random"
        
        // List of backup food facts in case API fails
        private val backupFacts = listOf(
            "Did you know? Eating breakfast boosts your metabolism!",
            "Honey never spoils! Archaeologists found 3000-year-old honey in Egyptian tombs that was still edible.",
            "Apples float in water because they are 25% air.",
            "Carrots were originally purple before being bred orange in the 17th century.",
            "Bananas are berries, but strawberries aren't!",
            "A medium-sized sweet potato has only 100 calories.",
            "Eating dark chocolate can improve your mood by releasing endorphins.",
            "Ginger can help reduce muscle pain and soreness.",
            "Cinnamon can help regulate blood sugar levels.",
            "Green tea contains compounds that can boost brain function."
        )

        // Main function to call for random food facts
        suspend fun getRandomFoodFact(): String {
            // Runs network code in the IO thread
            return withContext(Dispatchers.IO) {
                try {
                    // Builds API URL
                    val url = URL("$BASE_URL?apiKey=${BuildConfig.SPOONACULAR_API_KEY}")
                    val connection = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 5000
                        readTimeout = 5000
                        setRequestProperty("Accept", "application/json")
                    }

                    try {
                        val responseCode = connection.responseCode
                        // Checking if HTTP 200 is successful
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val response = connection.inputStream.bufferedReader().use { it.readText() }
                            // Parses the string into JSON
                            val jsonObject = JSONObject(response)
                            // Extract the text value from the JSON
                            jsonObject.getString("text")
                        } else {
                            Log.e(TAG, "API request failed with response code: $responseCode")
                            getRandomBackupFact()
                        }
                    } finally {
                        connection.disconnect()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Network error while fetching food fact", e)
                    getRandomBackupFact()
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching food fact", e)
                    getRandomBackupFact()
                }
            }
        }
        // Error handling
        private fun getRandomBackupFact(): String {
            return backupFacts.random()
        }
    }
} 