package com.bodakesatish.ktor.di

import com.bodakesatish.ktor.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for providing ViewModel dependencies.
 * `viewModel` DSL scopes the MainViewModel to the lifecycle of the component it's injected into (e.g., Activity/Fragment).
 */
val viewModelModule = module {
    viewModel { MainViewModel(mfRepository = get()) } // 'get()' resolves MFService from Koin
}