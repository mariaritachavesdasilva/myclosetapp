package com.example.myclosetapp.ui.createoutfit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.model.*
import com.example.myclosetapp.data.repo.AuthRepository
import com.example.myclosetapp.data.repo.ClosetRepository
import com.example.myclosetapp.stylist.StylistEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CreateOutfitViewModel(
    initialWeather: WeatherItem?,
    initialOccasion: OccasionItem?,
    private val authRepository: AuthRepository,
    private val closetRepository: ClosetRepository,
    private val stylistEngine: StylistEngine
) : ViewModel() {

    private val _selectedOccasion = MutableStateFlow<OccasionItem?>(initialOccasion ?: OccasionItem.PASSEIO)
    val selectedOccasion: StateFlow<OccasionItem?> = _selectedOccasion.asStateFlow()

    private val _selectedStyle = MutableStateFlow<StyleItem?>(StyleItem.CASUAL)
    val selectedStyle: StateFlow<StyleItem?> = _selectedStyle.asStateFlow()

    private val _selectedWeather = MutableStateFlow<WeatherItem?>(initialWeather ?: WeatherItem.AMENO)
    val selectedWeather: StateFlow<WeatherItem?> = _selectedWeather.asStateFlow()

    private val _outfitName = MutableStateFlow("")
    val outfitName: StateFlow<String> = _outfitName.asStateFlow()

    private val _selectedItemIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedItemIds: StateFlow<Set<Long>> = _selectedItemIds.asStateFlow()

    private val _suggestions = MutableStateFlow<List<OutfitSuggestion>>(emptyList())
    val suggestions: StateFlow<List<OutfitSuggestion>> = _suggestions.asStateFlow()

    private val _selectedSuggestion = MutableStateFlow<OutfitSuggestion?>(null)
    val selectedSuggestion: StateFlow<OutfitSuggestion?> = _selectedSuggestion.asStateFlow()

    val allItems: StateFlow<List<ClothingItem>> = closetRepository.allItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPreferences: StateFlow<UserPreferences> = authRepository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    val recentLogs: StateFlow<List<WearLog>> = closetRepository.allWearLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onOccasionSelect(occasion: OccasionItem?) {
        _selectedOccasion.value = occasion
    }

    fun onStyleSelect(style: StyleItem?) {
        _selectedStyle.value = style
    }

    fun onWeatherSelect(weather: WeatherItem?) {
        _selectedWeather.value = weather
    }

    fun onNameChange(name: String) {
        _outfitName.value = name
    }

    fun toggleItemSelection(item: ClothingItem) {
        val current = _selectedItemIds.value.toMutableSet()
        if (current.contains(item.id)) {
            current.remove(item.id)
        } else {
            current.add(item.id)
        }
        _selectedItemIds.value = current
        _selectedSuggestion.value = null
    }

    fun generateSuggestions() {
        viewModelScope.launch {
            val closet = allItems.value
            val prefs = userPreferences.value
            val logs = recentLogs.value

            val request = StylistRequest(
                occasion = _selectedOccasion.value,
                style = _selectedStyle.value,
                weather = _selectedWeather.value
            )

            val result = stylistEngine.suggest(
                request = request,
                closet = closet,
                prefs = prefs,
                recentWornLogs = logs,
                limit = 5
            )

            _suggestions.value = result
            if (result.isNotEmpty()) {
                selectSuggestion(result.first())
            } else {
                _errorMessage.value = "Adicione mais peças ao guarda-roupa para gerar combinações variadas."
            }
        }
    }

    fun selectSuggestion(suggestion: OutfitSuggestion) {
        _selectedSuggestion.value = suggestion
        _selectedItemIds.value = suggestion.items.map { it.id }.toSet()
        if (_outfitName.value.isBlank()) {
            val occasionLabel = _selectedOccasion.value?.label ?: "Look"
            val styleLabel = _selectedStyle.value?.label ?: ""
            _outfitName.value = "$occasionLabel $styleLabel".trim()
        }
    }

    fun saveOutfit(onSuccess: (Long) -> Unit) {
        val ids = _selectedItemIds.value.toList()
        if (ids.isEmpty()) {
            _errorMessage.value = "Selecione pelo menos 1 peça para o look."
            return
        }
        val name = _outfitName.value.ifBlank { "Meu Look Especial" }

        viewModelScope.launch {
            val outfit = Outfit(
                name = name,
                occasion = _selectedOccasion.value,
                style = _selectedStyle.value,
                weather = _selectedWeather.value,
                itemIds = ids
            )
            val newId = closetRepository.saveOutfit(outfit)
            onSuccess(newId)
        }
    }

    class Factory(
        private val initialWeather: WeatherItem?,
        private val initialOccasion: OccasionItem?,
        private val authRepository: AuthRepository,
        private val closetRepository: ClosetRepository,
        private val stylistEngine: StylistEngine
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateOutfitViewModel(
                initialWeather,
                initialOccasion,
                authRepository,
                closetRepository,
                stylistEngine
            ) as T
        }
    }
}
