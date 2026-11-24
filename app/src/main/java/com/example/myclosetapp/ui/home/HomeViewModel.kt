package com.example.myclosetapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.model.*
import com.example.myclosetapp.data.repo.AuthRepository
import com.example.myclosetapp.data.repo.ClosetRepository
import kotlinx.coroutines.flow.*

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val closetRepository: ClosetRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = authRepository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    val itemsCount: StateFlow<Int> = closetRepository.itemsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val outfitsCount: StateFlow<Int> = closetRepository.outfitsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val favoriteItemsCount: StateFlow<Int> = closetRepository.favoriteItemsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentItems: StateFlow<List<ClothingItem>> = closetRepository.allItems
        .map { list -> list.take(6) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedWeather = MutableStateFlow(WeatherItem.AMENO)
    val selectedWeather: StateFlow<WeatherItem> = _selectedWeather.asStateFlow()

    private val _selectedOccasion = MutableStateFlow(OccasionItem.PASSEIO)
    val selectedOccasion: StateFlow<OccasionItem> = _selectedOccasion.asStateFlow()

    init {
        // Set default weather from user prefs when available
        authRepository.userPreferences.onEach { prefs ->
            _selectedWeather.value = prefs.defaultWeather
        }.launchIn(viewModelScope)
    }

    fun onWeatherSelect(weather: WeatherItem) {
        _selectedWeather.value = weather
    }

    fun onOccasionSelect(occasion: OccasionItem) {
        _selectedOccasion.value = occasion
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val closetRepository: ClosetRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(authRepository, closetRepository) as T
        }
    }
}
