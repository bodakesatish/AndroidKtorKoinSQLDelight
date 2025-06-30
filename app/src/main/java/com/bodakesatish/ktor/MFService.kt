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
class MFService {

    // Initialize the Ktor HttpClient.
    // The client is configured once when an instance of MFService is created.
    private val client = HttpClient(engineFactory = Android) { // Use the Android engine for Ktor
        // Install the ContentNegotiation plugin to handle JSON serialization/deserialization.
        install(ContentNegotiation) {
            json(Json { // Configure Kotlinx Serialization for JSON
                ignoreUnknownKeys =
                    true // Allows skipping JSON fields that are not defined in our data classes
                prettyPrint =
                    true       // Formats the JSON output for better readability (useful for debugging)
                isLenient =
                    true         // Allows parsing JSON that might be slightly non-compliant with strict JSON rules
            })
        }
        // Install the Logging plugin to log HTTP requests and responses.
        // This is very useful for debugging network interactions.
        install(Logging) {
            // You can customize the logger, level, and what gets logged.

            // 1. Specify a Logger instance.
            //    By default, Ktor uses its own logger. For Android, it's common to integrate with Logcat.
            logger =
                object : Logger { // Create an anonymous object implementing Ktor's Logger interface
                    override fun log(message: String) {
                        // Log messages with the tag "KtorClient" and error level (you can change the level, e.g., Log.d for debug).
                        Log.e("KtorClient", message)
                    }
                }
            // Example of using a custom logger from a library like Timber:
            // logger = object : Logger {
            //     override fun log(message: String) {
            //         Timber.tag("KtorClient").d(message)
            //     }
            // }

            // 2. Set the Logging Level.
            //    This determines how much detail is logged.
            level = LogLevel.ALL // Logs everything:
            // - Request and response status lines (e.g., "200 OK", "GET /path HTTP/1.1")
            // - Request and response headers (e.g., "Content-Type: application/json")
            // - Request and response bodies (the actual data being sent/received)
            // - Common informational messages from Ktor

            // Common LogLevel options:
            // level = LogLevel.INFO  // Logs request and response status lines.
            // level = LogLevel.HEADERS // Logs status lines and headers.
            // level = LogLevel.BODY    // Logs status lines, headers, and bodies (often used during development).
            // level = LogLevel.NONE    // Disables logging entirely.

            // 3. Filter logs (optional).
            //    You can specify a filter to log only specific requests based on their properties.
            // filter { request -> // 'request' is an HttpRequestBuilder
            //     request.url.host == "api.example.com" // Example: Only log requests to "api.example.com"
            // }

            // 4. Sanitize sensitive headers (optional, good for production).
            //    Prevents logging the values of sensitive headers like Authorization tokens.
            // sanitizeHeader { header -> header == HttpHeaders.Authorization } // Returns true if the header should be sanitized
        }
    }

    /**
     * Fetches a list of Mutual Fund schemes from the API.
     * This is a suspend function because it performs a network operation, which is asynchronous.
     *
     * @return A list of [MFScheme] objects. Returns an empty list if an error occurs.
     */
    suspend fun getMFSchemes(): List<MFScheme> {
        return try {
            // Make an HTTP GET request to the specified URL.
            val response = client.get("https://api.mfapi.in/mf")

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
        client.close()
        // client.engine.close() // More forceful, closes the underlying engine immediately
    }
}