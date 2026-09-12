package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiMapsService {
    private const val TAG = "GeminiMapsService"
    private const val MODEL = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    data class GroundedResponse(
        val success: Boolean,
        val text: String,
        val placeNames: List<String> = emptyList(),
        val errorMessage: String? = null
    )

    /**
     * Queries Gemini 2.5 Flash using the Google Maps grounding tool.
     * Geographically constrained to Bengaluru for events, transit, venues, and routes.
     */
    suspend fun queryWithGoogleMaps(
        userPrompt: String,
        venueContext: String? = null
    ): GroundedResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext GroundedResponse(
                success = false,
                text = "Gemini API Key is not configured. Please add your key in the AI Studio Secrets panel.",
                errorMessage = "API_KEY_MISSING"
            )
        }

        try {
            val endpoint = "$BASE_URL/$MODEL:generateContent?key=$apiKey"

            val systemContext = "You are the official Bengaluru Events & Location Navigator. " +
                    "Focus strictly on Bengaluru (Bangalore), Karnataka, India. " +
                    "Use Google Maps data to provide accurate venue details, nearest Namma Metro stations (Purple/Green line), " +
                    "bus routes, parking tips, traffic landmarks, and verified route information for marathons, hackathons, and community meetups in Bengaluru."

            val fullPrompt = if (!venueContext.isNullOrBlank()) {
                "$systemContext\n\nSpecific Venue Context in Bengaluru: $venueContext\n\nUser Question: $userPrompt"
            } else {
                "$systemContext\n\nUser Question: $userPrompt"
            }

            // Build JSON payload with googleMaps tool
            val rootJson = JSONObject()

            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", fullPrompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            rootJson.put("contents", contentsArray)

            // Add Grounding with Google Maps tool
            val toolsArray = JSONArray()
            val googleMapsTool = JSONObject()
            googleMapsTool.put("googleMaps", JSONObject())
            toolsArray.put(googleMapsTool)
            rootJson.put("tools", toolsArray)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string()

            if (!response.isSuccessful || responseString == null) {
                Log.e(TAG, "Gemini API error: ${response.code} body: $responseString")
                return@withContext GroundedResponse(
                    success = false,
                    text = "Could not fetch Google Maps grounded details at this moment (HTTP ${response.code}).",
                    errorMessage = "HTTP_${response.code}"
                )
            }

            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext GroundedResponse(
                    success = false,
                    text = "No response generated from Google Maps Grounding.",
                    errorMessage = "NO_CANDIDATE"
                )
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textBuilder = StringBuilder()

            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val p = parts.getJSONObject(i)
                    val txt = p.optString("text", "")
                    if (txt.isNotEmpty()) {
                        textBuilder.append(txt)
                    }
                }
            }

            // Extract any grounding metadata places if present
            val places = mutableListOf<String>()
            val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
            if (groundingMetadata != null) {
                val chunks = groundingMetadata.optJSONArray("groundingChunks")
                if (chunks != null) {
                    for (i in 0 until chunks.length()) {
                        val chunk = chunks.optJSONObject(i)
                        val web = chunk?.optJSONObject("web")
                        val title = web?.optString("title")
                        if (!title.isNullOrBlank() && !places.contains(title)) {
                            places.add(title)
                        }
                    }
                }
            }

            val resultText = textBuilder.toString().ifEmpty { "Verified location information received." }

            GroundedResponse(
                success = true,
                text = resultText,
                placeNames = places
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Gemini with Google Maps", e)
            GroundedResponse(
                success = false,
                text = "Network or service error while consulting Google Maps: ${e.localizedMessage}",
                errorMessage = e.message
            )
        }
    }
}
