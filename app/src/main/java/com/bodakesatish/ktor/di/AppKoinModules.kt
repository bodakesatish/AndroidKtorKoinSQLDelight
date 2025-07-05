package com.bodakesatish.ktor.di

import com.bodakesatish.ktor.data.di.apiModule
import com.bodakesatish.ktor.data.di.dataSourceModuleKoin
import com.bodakesatish.ktor.data.di.databaseModule
import com.bodakesatish.ktor.data.di.networkModule

/**
 * List of all Koin modules to be started by the application.
 */
val appModules = listOf(databaseModule, networkModule, apiModule, viewModelModule, dispatchersModule, dataSourceModuleKoin)