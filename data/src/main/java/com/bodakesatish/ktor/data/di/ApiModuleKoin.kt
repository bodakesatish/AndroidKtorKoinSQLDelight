package com.bodakesatish.ktor.data.di

import com.bodakesatish.ktor.data.source.remote.api.SchemeApiService
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for providing data-related dependencies.
 * MFService is declared as a singleton here.
 * It depends on the HttpClient provided by `networkModule`.
 */
val apiModule = module {
    single {
        // MFService is now primarily a remote data source
        SchemeApiService(
            httpClient = get(),
            // Tell Koin to get the dispatcher qualified with "IODispatcher"
            ioDispatcher = get(named("IODispatcher"))
        )// 'get()' resolves HttpClient from Koin
    }
}