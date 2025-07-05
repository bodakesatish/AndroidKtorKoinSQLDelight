package com.bodakesatish.ktor.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bodakesatish.ktor.domain.model.SchemeModel
import com.bodakesatish.ktor.domain.repository.SchemeRepository
import com.bodakesatish.ktor.domain.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// UI State data class (you might have this from previous refactoring)
data class SchemesUiState(
    val schemes: List<SchemeModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// MFService will be injected by Koin
class MainViewModel(
    private val mfRepository: SchemeRepository // Inject Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SchemesUiState())
    val uiState: StateFlow<SchemesUiState> = _uiState.asStateFlow()

    init {
        fetchMFSchemes()
    }

    fun fetchMFSchemes(forceRefresh: Boolean = false) {
        mfRepository.observeSchemeList(isForceRefresh = forceRefresh)
            .onEach { resource ->
                _uiState.value = when (resource) {
                    is NetworkResult.Loading -> {
                        _uiState.value.copy(isLoading = true, errorMessage = null)
                    }
                    is NetworkResult.Success -> {
                        _uiState.value.copy(
                            isLoading = false,
                            schemes = resource.data,
                            errorMessage = if (resource.data.isEmpty() && !_uiState.value.isLoading) "No schemes found." else null
                        )
                    }
                    is NetworkResult.Error -> {
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage = resource.message
                            // Potentially keep displaying old data if available:
                            // schemes = if (resource.data != null) resource.data else _uiState.value.schemes
                        )
                    }
                }
            }
            .launchIn(viewModelScope) // Collect the Flow in the ViewModel's scope

    }


    // Example function for pull-to-refresh or explicit refresh button
    fun refreshSchemes() {
        fetchMFSchemes(forceRefresh = true)
    }

    fun clearLocalCache() {
        viewModelScope.launch {
            mfRepository.clearCache()
            // Optionally, re-fetch or update UI to reflect empty state if desired
            // fetchMFSchemes(forceRefresh = false) // This would try to fetch from network again if cache is empty
            _uiState.value = _uiState.value.copy(schemes = emptyList(), errorMessage = "Cache cleared.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        //mfService.closeClient() // Close the Ktor client when ViewModel is no longer needed
        // It's generally better if the lifecycle of singletons (like HttpClient via MFService)
        // is managed by Koin itself rather than explicitly closing here.
        // If mfService.client is a true singleton, closing it here could affect other parts
        // of the app if they were also using it (though less likely with ViewModel scope).
        // Consider removing this if Koin manages HttpClient as a global singleton.
        // mfService.closeClient()
        Log.d("MainViewModel", "onCleared called.")
    }
}