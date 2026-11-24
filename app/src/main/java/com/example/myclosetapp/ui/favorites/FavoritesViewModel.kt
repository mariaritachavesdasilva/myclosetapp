package com.example.myclosetapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.model.ClothingItem
import com.example.myclosetapp.data.model.Outfit
import com.example.myclosetapp.data.repo.ClosetRepository
import com.example.myclosetapp.ui.outfits.OutfitWithItems
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val closetRepository: ClosetRepository
) : ViewModel() {

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    val favoriteItems: StateFlow<List<ClothingItem>> = closetRepository.favoriteItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteOutfitsWithItems: StateFlow<List<OutfitWithItems>> = combine(
        closetRepository.favoriteOutfits,
        closetRepository.allItems
    ) { favoriteOutfits, allItems ->
        val itemMap = allItems.associateBy { it.id }
        favoriteOutfits.map { outfit ->
            val outfitItems = outfit.itemIds.mapNotNull { itemMap[it] }
            OutfitWithItems(outfit = outfit, items = outfitItems)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
    }

    fun toggleItemFavorite(item: ClothingItem) {
        viewModelScope.launch {
            closetRepository.saveItem(item.copy(isFavorite = !item.isFavorite))
        }
    }

    fun toggleOutfitFavorite(outfit: Outfit) {
        viewModelScope.launch {
            closetRepository.toggleOutfitFavorite(outfit)
        }
    }

    class Factory(private val closetRepository: ClosetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoritesViewModel(closetRepository) as T
        }
    }
}
