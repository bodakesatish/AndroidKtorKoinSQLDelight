package com.bodakesatish.ktor

import android.app.Application
import com.bodakesatish.ktor.di.appModules // Your Koin modules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class KtorApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Use androidLogger for Koin logging integrated with Android's Logcat.
            // Level.ERROR for production, Level.DEBUG for development.
            androidLogger(Level.DEBUG)

            // Provide the Android application context to Koin.
            androidContext(this@KtorApplication)

            // Load your Koin modules.
            modules(appModules)
        }
    }
}