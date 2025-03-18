package com.example.mealsmatter.api

import com.example.mealsmatter.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.HttpURLConnection
import java.io.IOException
import android.util.Log

class FoodFactsApi {
    companion object {
        private const val TAG = "FoodFactsApi"
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

        suspend fun getRandomFoodFact(): String {
            return withContext(Dispatchers.IO) {
                try {
                    val url = URL("$BASE_URL?apiKey=${BuildConfig.SPOONACULAR_API_KEY}")
                    val connection = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 5000
                        readTimeout = 5000
                        setRequestProperty("Accept", "application/json")
                    }

                    try {
                        val responseCode = connection.responseCode
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val response = connection.inputStream.bufferedReader().use { it.readText() }
                            val jsonObject = JSONObject(response)
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

        private fun getRandomBackupFact(): String {
            return backupFacts.random()
        }
    }
} 