package com.bodakesatish.ktor

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// MFService will be injected by Koin
class MainViewModel(private val mfService: MFService) : ViewModel() {

    private val _mfSchemes = MutableLiveData<List<MFScheme>>()
    val mfSchemes: LiveData<List<MFScheme>> = _mfSchemes

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchMFSchemes()
    }

    fun fetchMFSchemes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = "" // Clear previous error messages
            try {
                val schemes = mfService.getMFSchemes() // Use the injected mfService
                _mfSchemes.value = schemes
                if (schemes.isEmpty()) {
                    // Optional: Set a specific message if the list is empty but no network error
                     _errorMessage.value = "No schemes found."
                }
            } catch (e: Exception) {
                // This catch block might be redundant if mfService.getMFSchemes() already handles
                // its internal exceptions and returns an empty list or a wrapped error type.
                // For simplicity here, we keep it as a fallback.
                _errorMessage.value = "Error fetching MF schemes: ${e.message}"
                _mfSchemes.value = emptyList() // Ensure list is empty on error
                Log.e("MainViewModel", "Failed to fetch schemes", e)
            } finally {
                _isLoading.value = false
            }
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