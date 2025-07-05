package com.bodakesatish.ktor.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

// Usually in a module dedicated to dispatchers or common utilities
val dispatchersModule = module {
    // Provide Dispatchers.IO and qualify it with a name
    single<CoroutineDispatcher>(named("IODispatcher")) { // You choose the string name
        Dispatchers.IO
    }

    // If you had a Main dispatcher:
    // single<CoroutineDispatcher>(named("MainDispatcher")) {
    //     Dispatchers.Main
    // }
}