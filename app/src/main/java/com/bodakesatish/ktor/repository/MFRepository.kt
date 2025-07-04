package com.bodakesatish.ktor.repository

import android.util.Log

import com.bodakesatish.ktor.MFScheme // Your domain model
import com.bodakesatish.ktor.MFService // Your remote service
import com.bodakesatish.ktor.cache.MFSchemeQueries // SQLDelight generated queries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import app.cash.sqldelight.coroutines.asFlow // SQLDelight extension for Flow
import app.cash.sqldelight.coroutines.mapToList // SQLDelight extension
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.measureTime

// Represents the result of an operation, handling success, error, and loading.
// You might already have this from previous refactoring.
sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String, val cause: Throwable? = null) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}

class MFSchemeRepository(
    private val remoteService: MFService,
    private val schemeQueries: MFSchemeQueries
) {

    /**
     * Gets MF schemes.
     * Tries to fetch from the network. If successful, updates the cache.
     * Always returns data from the cache as a Flow, which will update if the cache changes.
     *
     * @param forceRefresh If true, fetches from network even if cache might seem fresh.
     */
    fun getSchemes(forceRefresh: Boolean = false): Flow<Resource<List<MFScheme>>> = flow {
        emit(Resource.Loading)

        // Optionally, load initial data from cache immediately
        // val cachedSchemes = loadSchemesFromCache()
        // if (cachedSchemes.isNotEmpty()) {
        //    emit(Resource.Success(cachedSchemes))
        // }

        // Determine if network fetch is needed
        // For simplicity, we fetch on forceRefresh or if cache is empty.
        // A more robust solution would involve checking `lastFetched` timestamp.
        val needsNetworkFetch = forceRefresh || schemeQueries.count().executeAsOne() == 0L

        if (needsNetworkFetch) {
            try {
                val networkSchemes = remoteService.getMFSchemes()
                if (networkSchemes.isNotEmpty()) {
                    // Save to database
                    withContext(Dispatchers.IO) { // Perform database operations on IO dispatcher
                        val executionTime: Long = measureTimeMillis {
                            schemeQueries.transaction { // Use transaction for multiple inserts
                                // schemeQueries.deleteAll() // Optional: Clear old cache completely

                                networkSchemes.forEach { scheme ->
                                    schemeQueries.insertOrReplace(
                                        schemeCode = scheme.schemeCode.toLong(), // Ensure type match (INTEGER)
                                        schemeName = scheme.schemeName,
//                                    isinGrowth = scheme.isinGrowth,
//                                    isinDivReinvestment = scheme.isinDivReinvestment,
                                        lastFetched = System.currentTimeMillis() // Store current time
                                    )
                                }
                            }
                        }
                        Log.e("MFRepository","Execution time: $executionTime")
                    }
                } else if (forceRefresh) {
                    // If forced refresh yields no data, it could be an error or genuinely no data.
                    // If cache was also empty, emit error.
                    if (schemeQueries.count().executeAsOne() == 0L) {
                        emit(Resource.Error("No schemes found from network and cache is empty."))
                    }
                }
            } catch (e: Exception) {
                Log.e("MFSchemeRepository", "Network error fetching schemes", e)
                // If network fails, still try to emit cached data if any
                if (schemeQueries.count().executeAsOne() == 0L) {
                    emit(Resource.Error("Network error: ${e.message}", e))
                }
            }
        }

        // Always emit the latest from the database as a Flow
        // This Flow will automatically update when the database content changes.
        try {
            // Use SQLDelight's Flow extension
            schemeQueries.selectAll().asFlow().mapToList(Dispatchers.IO).collect { entities ->
                val domainModels = entities.map { entity ->
                    MFScheme( // Map from MFSchemeEntity to your MFScheme domain model
                        schemeCode = entity.schemeCode.toInt(),
                        schemeName = entity.schemeName,
//                        isinGrowth = entity.isinGrowth,
//                        isinDivReinvestment = entity.isinDivReinvestment
                        // lastFetched is not part of MFScheme domain model
                    )
                }
                emit(Resource.Success(domainModels))
            }
        } catch (e: Exception) {
            Log.e("MFSchemeRepository", "Database error emitting schemes", e)
            emit(Resource.Error("Database error: ${e.message}", e))
        }

    }.flowOn(Dispatchers.IO) // Ensure most of the flow logic runs on IO

    // Helper to load from cache synchronously (example)
    private suspend fun loadSchemesFromCache(): List<MFScheme> = withContext(Dispatchers.IO) {
        schemeQueries.selectAll().executeAsList().map { entity ->
            MFScheme(
                schemeCode = entity.schemeCode.toInt(),
                schemeName = entity.schemeName,
//                isinGrowth = entity.isinGrowth,
//                isinDivReinvestment = entity.isinDivReinvestment
            )
        }
    }

    suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            schemeQueries.deleteAll()
        }
    }
}