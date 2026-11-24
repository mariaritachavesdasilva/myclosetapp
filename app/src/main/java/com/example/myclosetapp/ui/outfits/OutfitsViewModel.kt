package com.example.myclosetapp.ui.outfits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.model.ClothingItem
import com.example.myclosetapp.data.model.Outfit
import com.example.myclosetapp.data.repo.ClosetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OutfitWithItems(
    val outfit: Outfit,
    val items: List<ClothingItem>
)

class OutfitsViewModel(
    private val closetRepository: ClosetRepository
) : ViewModel() {

    val outfitsWithItems: StateFlow<List<OutfitWithItems>> = combine(
        closetRepository.allOutfits,
        closetRepository.allItems
    ) { outfits, allItems ->
        val itemMap = allItems.associateBy { it.id }
        outfits.map { outfit ->
            val outfitItems = outfit.itemIds.mapNotNull { itemMap[it] }
            OutfitWithItems(outfit = outfit, items = outfitItems)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(outfit: Outfit) {
        viewModelScope.launch {
            closetRepository.toggleOutfitFavorite(outfit)
        }
    }

    class Factory(private val closetRepository: ClosetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OutfitsViewModel(closetRepository) as T
        }
    }
}
