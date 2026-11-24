package com.example.myclosetapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myclosetapp.data.model.WearLog
import com.example.myclosetapp.data.repo.ClosetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val closetRepository: ClosetRepository
) : ViewModel() {

    val wearLogs: StateFlow<List<WearLog>> = closetRepository.allWearLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showClearDialog = MutableStateFlow(false)
    val showClearDialog: StateFlow<Boolean> = _showClearDialog.asStateFlow()

    fun deleteLog(log: WearLog) {
        viewModelScope.launch {
            closetRepository.deleteWearLog(log)
        }
    }

    fun onClearAllClick() {
        _showClearDialog.value = true
    }

    fun dismissClearDialog() {
        _showClearDialog.value = false
    }

    fun confirmClearAll() {
        viewModelScope.launch {
            closetRepository.clearHistory()
            _showClearDialog.value = false
        }
    }

    class Factory(private val closetRepository: ClosetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(closetRepository) as T
        }
    }
}
