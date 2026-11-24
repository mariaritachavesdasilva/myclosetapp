package com.example.myclosetapp.ui.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.model.Category
import com.example.myclosetapp.data.model.ClothingItem
import com.example.myclosetapp.data.repo.ClosetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val closetRepository: ClosetRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    val filteredItems: StateFlow<List<ClothingItem>> = combine(
        closetRepository.allItems,
        _searchQuery,
        _selectedCategory,
        _onlyFavorites
    ) { items, query, category, favorites ->
        items.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.brand.contains(query, ignoreCase = true) ||
                    item.notes.contains(query, ignoreCase = true)

            val matchesCategory = (category == null) || (item.category == category)
            val matchesFavorites = !favorites || item.isFavorite

            matchesQuery && matchesCategory && matchesFavorites
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: Category?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun toggleOnlyFavorites() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun toggleFavorite(item: ClothingItem) {
        viewModelScope.launch {
            closetRepository.saveItem(item.copy(isFavorite = !item.isFavorite))
        }
    }

    class Factory(private val closetRepository: ClosetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ItemsViewModel(closetRepository) as T
        }
    }
}
