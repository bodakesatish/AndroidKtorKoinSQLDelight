package com.bodakesatish.ktor.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.bodakesatish.ktor.data.SchemeDatabase
import com.bodakesatish.ktor.data.SchemeEntityQueries
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Koin module for providing database-related dependencies.
 */
val databaseModule = module {
    // Provide the AndroidSqliteDriver
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = SchemeDatabase.Schema, // Generated schema
            context = androidApplication(), // Provides Application context
            name = "mfschemes.db" // Database file name
        )
    }

    // Provide the AppDatabase instance
    single<SchemeDatabase> {
        SchemeDatabase(driver = get()) // 'get()' resolves SqlDriver
    }

    // Provide the SchemeEntityQueries (for interacting with the SchemeEntity table)
    single<SchemeEntityQueries> {
        val database = get<SchemeDatabase>() // Get the AppDatabase instance
        database.schemeEntityQueries // Access the generated queries property
    }
}