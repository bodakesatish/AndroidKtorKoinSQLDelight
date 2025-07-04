package com.bodakesatish.ktor.di

import android.util.Log
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.bodakesatish.ktor.MFService
import com.bodakesatish.ktor.MainViewModel
import com.bodakesatish.ktor.cache.AppDatabase
import com.bodakesatish.ktor.cache.MFSchemeQueries
import com.bodakesatish.ktor.repository.MFSchemeRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for providing database-related dependencies.
 */
val databaseModule = module {
    // Provide the AndroidSqliteDriver
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = AppDatabase.Schema, // Generated schema
            context = androidApplication(), // Provides Application context
            name = "mfschemes.db" // Database file name
        )
    }

    // Provide the AppDatabase instance
    single<AppDatabase> {
        AppDatabase(driver = get()) // 'get()' resolves SqlDriver
    }

    // Provide the MFSchemeEntityQueries (for interacting with the MFSchemeEntity table)
    single<MFSchemeQueries> {
        val database = get<AppDatabase>() // Get the AppDatabase instance
        database.mFSchemeQueries // Access the generated queries property
    }
}


/**
 * Koin module for providing the Ktor HttpClient.
 * Declared as a singleton (`single`) so only one instance is created and shared.
 */
val networkModule = module {
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorClient", message) // Changed to Log.d for debug clarity
                    }
                }
                level = LogLevel.BODY // Log BODY for detailed request/response during development
            }
        }
    }
}

/**
 * Koin module for providing data-related dependencies.
 * MFService is declared as a singleton here.
 * It depends on the HttpClient provided by `networkModule`.
 */
val dataModule = module {
    single {
        // MFService is now primarily a remote data source
        MFService(httpClient = get())// 'get()' resolves HttpClient from Koin
    }
    // Provide the Repository
    single {
        MFSchemeRepository(
            remoteService = get(), // Gets MFService
            schemeQueries = get()  // Gets MFSchemeEntityQueries
        )
    }
}

/**
 * Koin module for providing ViewModel dependencies.
 * `viewModel` DSL scopes the MainViewModel to the lifecycle of the component it's injected into (e.g., Activity/Fragment).
 */
val viewModelModule = module {
    viewModel { MainViewModel(mfRepository = get()) } // 'get()' resolves MFService from Koin
}

/**
 * List of all Koin modules to be started by the application.
 */
val appModules = listOf(databaseModule, networkModule, dataModule, viewModelModule)