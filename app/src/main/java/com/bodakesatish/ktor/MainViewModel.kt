package com.bodakesatish.ktor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val mfService = MFService()

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
            try {
                val schemes = mfService.getMFSchemes()
                _mfSchemes.value = schemes
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching MF schemes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mfService.closeClient() // Close the Ktor client when ViewModel is no longer needed
    }
}