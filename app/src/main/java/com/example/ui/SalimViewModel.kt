package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.AppContainer
import com.example.data.local.entities.LookupHistoryEntity
import com.example.data.local.entities.SavedNumberEntity
import com.example.data.model.NormalizedPhoneNumber
import com.example.lookup.MergedLookupResult
import com.example.util.RegionInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SalimViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val _phoneInput = MutableStateFlow("")
    val phoneInput: StateFlow<String> = _phoneInput.asStateFlow()

    private val _selectedRegion = MutableStateFlow("US")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

    private val _isLookingUp = MutableStateFlow(false)
    val isLookingUp: StateFlow<Boolean> = _isLookingUp.asStateFlow()

    private val _currentResult = MutableStateFlow<MergedLookupResult?>(null)
    val currentResult: StateFlow<MergedLookupResult?> = _currentResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    val supportedRegions: List<RegionInfo> = container.normalizer.getSupportedRegions()

    val history: StateFlow<List<LookupHistoryEntity>> = container.historyRepository.history
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedNumbers: StateFlow<List<SavedNumberEntity>> = container.localDirectoryRepository.savedNumbers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val useDeviceContacts: StateFlow<Boolean> = container.settingsRepository.useDeviceContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val askBeforeOnlineLookup: StateFlow<Boolean> = container.settingsRepository.askBeforeOnlineLookup
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val enableAuthorizedProvider: StateFlow<Boolean> = container.settingsRepository.enableAuthorizedProvider
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val saveLookupHistory: StateFlow<Boolean> = container.settingsRepository.saveLookupHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val historyRetentionDays: StateFlow<Int> = container.settingsRepository.historyRetentionDays
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 30)

    val defaultCountry: StateFlow<String> = container.settingsRepository.defaultCountry
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "US")

    val hapticFeedback: StateFlow<Boolean> = container.settingsRepository.hapticFeedbackEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        viewModelScope.launch {
            val defaultRegion = container.settingsRepository.defaultCountry.first()
            _selectedRegion.value = defaultRegion
        }
    }

    fun onPhoneInputChanged(input: String) {
        _phoneInput.value = input
        _errorMessage.value = null
    }

    fun onRegionSelected(region: RegionInfo) {
        _selectedRegion.value = region.regionCode
    }

    fun onPaste(pasted: String) {
        _phoneInput.value = pasted.trim()
        _errorMessage.value = null
    }

    fun onClearInput() {
        _phoneInput.value = ""
        _errorMessage.value = null
    }

    fun clearResult() {
        _currentResult.value = null
    }

    fun dismissUserMessage() {
        _userMessage.value = null
    }

    fun lookupNumber(overrideNumber: String? = null, onComplete: ((MergedLookupResult) -> Unit)? = null) {
        val input = overrideNumber ?: _phoneInput.value
        if (input.isBlank()) {
            _errorMessage.value = "Please enter or paste a phone number."
            return
        }

        viewModelScope.launch {
            _isLookingUp.value = true
            _errorMessage.value = null

            val normalizeResult = container.normalizePhoneNumberUseCase(input, _selectedRegion.value)
            normalizeResult.fold(
                onSuccess = { normalized ->
                    try {
                        val merged = container.lookupNumberIdentityUseCase(normalized, saveToHistory = true)
                        _currentResult.value = merged
                        onComplete?.invoke(merged)
                    } catch (e: Exception) {
                        _errorMessage.value = "Lookup failed: ${e.message}"
                    }
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Invalid phone number format."
                }
            )

            _isLookingUp.value = false
        }
    }

    fun saveNumber(
        normalizedNumber: String,
        displayNumber: String,
        label: String,
        notes: String?,
        category: String?,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            container.saveNumberIdentityUseCase(
                normalizedNumber = normalizedNumber,
                displayNumber = displayNumber,
                label = label,
                notes = notes,
                category = category,
                isFavorite = isFavorite
            )
            _userMessage.value = "Saved to your local directory."
            // Refresh current result if it's viewing this number
            if (_currentResult.value?.primaryResult?.normalizedNumber == normalizedNumber) {
                val normalizeResult = container.normalizePhoneNumberUseCase(normalizedNumber, _selectedRegion.value)
                normalizeResult.getOrNull()?.let {
                    _currentResult.value = container.lookupNumberIdentityUseCase(it, saveToHistory = false)
                }
            }
        }
    }

    fun deleteSavedNumber(entity: SavedNumberEntity) {
        viewModelScope.launch {
            container.localDirectoryRepository.deleteSavedNumber(entity)
            _userMessage.value = "Entry removed from local directory."
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            container.deleteLookupHistoryUseCase.deleteItem(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            container.deleteLookupHistoryUseCase.clearAll()
            _userMessage.value = "Lookup history cleared."
        }
    }

    fun submitReport(
        normalizedNumber: String,
        reportedName: String?,
        reportType: String,
        reasonDescription: String
    ) {
        viewModelScope.launch {
            container.reportsRepository.submitReport(
                normalizedNumber = normalizedNumber,
                reportedName = reportedName,
                reportType = reportType,
                reasonDescription = reasonDescription
            )
            _userMessage.value = "Report submitted. Thank you for maintaining data integrity."
        }
    }

    fun setUseDeviceContacts(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setUseDeviceContacts(enabled)
        }
    }

    fun setAskBeforeOnlineLookup(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setAskBeforeOnlineLookup(enabled)
        }
    }

    fun setEnableAuthorizedProvider(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setEnableAuthorizedProvider(enabled)
        }
    }

    fun setSaveLookupHistory(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setSaveLookupHistory(enabled)
        }
    }

    fun setHistoryRetentionDays(days: Int) {
        viewModelScope.launch {
            container.settingsRepository.setHistoryRetentionDays(days)
            container.historyRepository.pruneOldHistory(days)
        }
    }

    fun setDefaultCountry(countryCode: String) {
        viewModelScope.launch {
            container.settingsRepository.setDefaultCountry(countryCode)
            _selectedRegion.value = countryCode
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.setHapticFeedback(enabled)
        }
    }

    fun deleteAllLocalData() {
        viewModelScope.launch {
            container.historyRepository.clearHistory()
            container.localDirectoryRepository.clearAll()
            container.reportsRepository.clearReports()
            _currentResult.value = null
            _userMessage.value = "All local data and history permanently deleted."
        }
    }

    companion object {
        fun provideFactory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SalimViewModel(container) as T
                }
            }
    }
}
