package com.example.myclosetapp.ui.itemdetail

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.image.ImageUtils
import com.example.myclosetapp.data.model.*
import com.example.myclosetapp.data.repo.ClosetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemEditViewModel(
    private val itemId: Long?,
    private val closetRepository: ClosetRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _category = MutableStateFlow(Category.BLUSAS)
    val category: StateFlow<Category> = _category.asStateFlow()

    private val _color = MutableStateFlow(ColorItem.OFF_WHITE)
    val color: StateFlow<ColorItem> = _color.asStateFlow()

    private val _pattern = MutableStateFlow(PatternItem.LISA)
    val pattern: StateFlow<PatternItem> = _pattern.asStateFlow()

    private val _styles = MutableStateFlow<Set<StyleItem>>(setOf(StyleItem.CASUAL))
    val styles: StateFlow<Set<StyleItem>> = _styles.asStateFlow()

    private val _occasions = MutableStateFlow<Set<OccasionItem>>(setOf(OccasionItem.PASSEIO))
    val occasions: StateFlow<Set<OccasionItem>> = _occasions.asStateFlow()

    private val _seasons = MutableStateFlow<Set<SeasonItem>>(setOf(SeasonItem.PRIMAVERA, SeasonItem.VERAO))
    val seasons: StateFlow<Set<SeasonItem>> = _seasons.asStateFlow()

    private val _brand = MutableStateFlow("")
    val brand: StateFlow<String> = _brand.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath: StateFlow<String?> = _imagePath.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var existingItem: ClothingItem? = null

    val isEditing: Boolean = itemId != null && itemId > 0

    init {
        if (itemId != null && itemId > 0) {
            viewModelScope.launch {
                val item = closetRepository.getItemById(itemId)
                if (item != null) {
                    existingItem = item
                    _name.value = item.name
                    _category.value = item.category
                    _color.value = item.color
                    _pattern.value = item.pattern
                    _styles.value = item.styles
                    _occasions.value = item.occasions
                    _seasons.value = item.seasons
                    _brand.value = item.brand
                    _notes.value = item.notes
                    _imagePath.value = item.imagePath
                    _isFavorite.value = item.isFavorite
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onCategoryChange(value: Category) {
        _category.value = value
    }

    fun onColorChange(value: ColorItem) {
        _color.value = value
    }

    fun onPatternChange(value: PatternItem) {
        _pattern.value = value
    }

    fun toggleStyle(style: StyleItem) {
        val current = _styles.value.toMutableSet()
        if (current.contains(style)) {
            if (current.size > 1) current.remove(style)
        } else {
            current.add(style)
        }
        _styles.value = current
    }

    fun toggleOccasion(occasion: OccasionItem) {
        val current = _occasions.value.toMutableSet()
        if (current.contains(occasion)) {
            if (current.size > 1) current.remove(occasion)
        } else {
            current.add(occasion)
        }
        _occasions.value = current
    }

    fun toggleSeason(season: SeasonItem) {
        val current = _seasons.value.toMutableSet()
        if (current.contains(season)) {
            if (current.size > 1) current.remove(season)
        } else {
            current.add(season)
        }
        _seasons.value = current
    }

    fun onBrandChange(value: String) {
        _brand.value = value
    }

    fun onNotesChange(value: String) {
        _notes.value = value
    }

    fun onFavoriteChange(value: Boolean) {
        _isFavorite.value = value
    }

    fun onImagePicked(context: Context, uri: Uri) {
        viewModelScope.launch {
            val savedPath = ImageUtils.saveImageToInternalStorage(context, uri)
            if (savedPath != null) {
                // Delete previous temp image if edited
                if (_imagePath.value != null && _imagePath.value != existingItem?.imagePath) {
                    ImageUtils.deleteImageFile(_imagePath.value)
                }
                _imagePath.value = savedPath
            } else {
                _errorMessage.value = "Não foi possível carregar a imagem selecionada."
            }
        }
    }

    fun onDeleteClick() {
        _showDeleteDialog.value = true
    }

    fun dismissDeleteDialog() {
        _showDeleteDialog.value = false
    }

    fun confirmDelete(onSuccess: () -> Unit) {
        val item = existingItem ?: return
        viewModelScope.launch {
            closetRepository.deleteItem(item)
            ImageUtils.deleteImageFile(item.imagePath)
            _showDeleteDialog.value = false
            onSuccess()
        }
    }

    fun saveItem(onSuccess: () -> Unit) {
        val trimmedName = _name.value.trim()
        if (trimmedName.isBlank()) {
            _errorMessage.value = "Informe um nome para a peça."
            return
        }

        viewModelScope.launch {
            val itemToSave = ClothingItem(
                id = existingItem?.id ?: 0L,
                name = trimmedName,
                category = _category.value,
                color = _color.value,
                pattern = _pattern.value,
                styles = _styles.value,
                occasions = _occasions.value,
                seasons = _seasons.value,
                brand = _brand.value.trim(),
                notes = _notes.value.trim(),
                imagePath = _imagePath.value,
                isFavorite = _isFavorite.value,
                timesWorn = existingItem?.timesWorn ?: 0,
                lastWornTimestamp = existingItem?.lastWornTimestamp,
                createdAt = existingItem?.createdAt ?: System.currentTimeMillis()
            )

            closetRepository.saveItem(itemToSave)
            onSuccess()
        }
    }

    class Factory(
        private val itemId: Long?,
        private val closetRepository: ClosetRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ItemEditViewModel(itemId, closetRepository) as T
        }
    }
}
