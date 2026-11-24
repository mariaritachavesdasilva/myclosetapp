package com.example.myclosetapp.ui.outfitdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.model.ClothingItem
import com.example.myclosetapp.data.model.Outfit
import com.example.myclosetapp.data.repo.ClosetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OutfitDetailViewModel(
    private val outfitId: Long,
    private val closetRepository: ClosetRepository
) : ViewModel() {

    private val _outfit = MutableStateFlow<Outfit?>(null)
    val outfit: StateFlow<Outfit?> = _outfit.asStateFlow()

    private val _items = MutableStateFlow<List<ClothingItem>>(emptyList())
    val items: StateFlow<List<ClothingItem>> = _items.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        viewModelScope.launch {
            val outfitObj = closetRepository.getOutfitById(outfitId)
            if (outfitObj != null) {
                _outfit.value = outfitObj
                val outfitItems = closetRepository.getItemsByIds(outfitObj.itemIds)
                _items.value = outfitItems
            }
        }
    }

    fun toggleFavorite() {
        val currentOutfit = _outfit.value ?: return
        viewModelScope.launch {
            closetRepository.toggleOutfitFavorite(currentOutfit)
            _outfit.value = currentOutfit.copy(isFavorite = !currentOutfit.isFavorite)
        }
    }

    fun markAsWornToday() {
        val currentOutfit = _outfit.value ?: return
        viewModelScope.launch {
            closetRepository.logOutfitWorn(
                outfitId = currentOutfit.id,
                title = currentOutfit.name,
                itemIds = currentOutfit.itemIds,
                occasion = currentOutfit.occasion
            )
            _outfit.value = currentOutfit.copy(timesWorn = currentOutfit.timesWorn + 1)
            _userMessage.value = "Look registrado no histórico de uso de hoje! ✨"
        }
    }

    fun dismissUserMessage() {
        _userMessage.value = null
    }

    fun onDeleteClick() {
        _showDeleteDialog.value = true
    }

    fun dismissDeleteDialog() {
        _showDeleteDialog.value = false
    }

    fun confirmDelete(onSuccess: () -> Unit) {
        val currentOutfit = _outfit.value ?: return
        viewModelScope.launch {
            closetRepository.deleteOutfit(currentOutfit)
            _showDeleteDialog.value = false
            onSuccess()
        }
    }

    class Factory(
        private val outfitId: Long,
        private val closetRepository: ClosetRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OutfitDetailViewModel(outfitId, closetRepository) as T
        }
    }
}
