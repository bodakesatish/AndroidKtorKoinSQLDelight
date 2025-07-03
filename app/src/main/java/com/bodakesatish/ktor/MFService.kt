package com.bodakesatish.ktor

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.* // Import the Logging plugin

/**
 * Service class responsible for making network requests related to Mutual Fund (MF) schemes.
 *
 * This class encapsulates the Ktor HttpClient setup and the specific API calls.
 * Note: In a more advanced architecture (like the one discussed with Koin/Hilt),
 * this class might be refactored into a DataSource, and the HttpClient
 * would typically be provided via dependency injection.
 */
class MFService(private val httpClient: HttpClient) {

    // Initialize the Ktor HttpClient.
    // The client is configured once when an instance of MFService is created.

    /**
     * Fetches a list of Mutual Fund schemes from the API.
     * This is a suspend function because it performs a network operation, which is asynchronous.
     *
     * @return A list of [MFScheme] objects. Returns an empty list if an error occurs.
     */
    suspend fun getMFSchemes(): List<MFScheme> {
        return try {
            // Make an HTTP GET request to the specified URL.
            val response = httpClient.get("https://api.mfapi.in/mf")

            // Deserialize the JSON response body directly into a List of MFScheme objects.
            // Ktor uses the ContentNegotiation plugin (configured with Kotlinx Serialization) for this.
            response.body<List<MFScheme>>()
        } catch (e: Exception) {
            // Handle any exceptions that occur during the network request or deserialization.
            // For production apps, more sophisticated error handling and logging would be appropriate.
            println("Error fetching MF schemes: ${e.message}") // Prints the error to the standard output (visible in Logcat's "System.out" filter)
            Log.e(
                "MFService",
                "Error fetching MF schemes",
                e
            ) // Also log the full exception to Logcat
            emptyList() // Return an empty list to indicate failure or no data
        }
    }

    /**
     * Closes the Ktor HttpClient.
     * It's important to close the client when it's no longer needed to release resources
     * (like connection pools and threads).
     *
     * In architectures using dependency injection frameworks (like Koin or Hilt),
     * the lifecycle of the HttpClient is often managed by the DI framework,
     * and manual closing might be handled differently (e.g., when a DI scope is destroyed).
     */
    fun closeClient() {
        // Only close if the client is active.
        // Ktor's client.close() is idempotent (safe to call multiple times).
        httpClient.close()
        Log.d("MFService", "Ktor HttpClient closed.")

    }
}