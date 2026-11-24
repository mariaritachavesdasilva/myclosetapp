package com.example.myclosetapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.model.*
import com.example.myclosetapp.data.repo.AuthRepository
import com.example.myclosetapp.data.repo.ClosetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val closetRepository: ClosetRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = authRepository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    val itemsCount: StateFlow<Int> = closetRepository.itemsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val outfitsCount: StateFlow<Int> = closetRepository.outfitsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _favoriteStyles = MutableStateFlow<Set<StyleItem>>(emptySet())
    val favoriteStyles: StateFlow<Set<StyleItem>> = _favoriteStyles.asStateFlow()

    private val _favoriteColors = MutableStateFlow<Set<ColorItem>>(emptySet())
    val favoriteColors: StateFlow<Set<ColorItem>> = _favoriteColors.asStateFlow()

    private val _defaultWeather = MutableStateFlow(WeatherItem.AMENO)
    val defaultWeather: StateFlow<WeatherItem> = _defaultWeather.asStateFlow()

    private val _avoidDaysThreshold = MutableStateFlow(3)
    val avoidDaysThreshold: StateFlow<Int> = _avoidDaysThreshold.asStateFlow()

    private val _isSavedMessageShown = MutableStateFlow(false)
    val isSavedMessageShown: StateFlow<Boolean> = _isSavedMessageShown.asStateFlow()

    init {
        authRepository.userPreferences.onEach { prefs ->
            _name.value = prefs.name
            _favoriteStyles.value = prefs.favoriteStyles
            _favoriteColors.value = prefs.favoriteColors
            _defaultWeather.value = prefs.defaultWeather
            _avoidDaysThreshold.value = prefs.avoidDaysThreshold
        }.launchIn(viewModelScope)
    }

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun toggleStyle(style: StyleItem) {
        val current = _favoriteStyles.value.toMutableSet()
        if (current.contains(style)) current.remove(style) else current.add(style)
        _favoriteStyles.value = current
    }

    fun toggleColor(color: ColorItem) {
        val current = _favoriteColors.value.toMutableSet()
        if (current.contains(color)) current.remove(color) else current.add(color)
        _favoriteColors.value = current
    }

    fun onWeatherSelect(weather: WeatherItem) {
        _defaultWeather.value = weather
    }

    fun onAvoidDaysChange(days: Int) {
        _avoidDaysThreshold.value = days.coerceIn(0, 7)
    }

    fun saveProfile() {
        viewModelScope.launch {
            authRepository.updatePreferences(
                name = _name.value.trim().ifBlank { "Ana" },
                favoriteStyles = _favoriteStyles.value,
                favoriteColors = _favoriteColors.value,
                defaultWeather = _defaultWeather.value,
                avoidDaysThreshold = _avoidDaysThreshold.value
            )
            _isSavedMessageShown.value = true
        }
    }

    fun dismissSavedMessage() {
        _isSavedMessageShown.value = false
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val closetRepository: ClosetRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(authRepository, closetRepository) as T
        }
    }
}
