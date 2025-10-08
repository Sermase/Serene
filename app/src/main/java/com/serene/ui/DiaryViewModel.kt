package com.serene.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.serene.SereneApplication
import com.serene.data.DiaryRepository
import com.serene.data.EntryType
import com.serene.data.RelationshipEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime

class DiaryViewModel(
    private val repository: DiaryRepository
) : ViewModel() {

    private val _currentEntry = MutableStateFlow(RelationshipEntry())
    val currentEntry: StateFlow<RelationshipEntry> = _currentEntry.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    init {
        observeEntries()
        refreshEntries()
    }

    private fun observeEntries() {
        viewModelScope.launch {
            repository.entries.collect { list ->
                _uiState.update { it.copy(entries = list) }
            }
        }
    }

    private fun refreshEntries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.refreshEntries()
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "Ocurrió un error al sincronizar")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEntryTypeSelected(type: EntryType) {
        _currentEntry.update { it.copy(type = type) }
    }

    fun onDateChanged(date: LocalDate) {
        _currentEntry.update { it.copy(date = date) }
    }

    fun onActivityChanged(activity: String) {
        _currentEntry.update { it.copy(activity = activity) }
    }

    fun onGenuineDesireChanged(value: Int) {
        _currentEntry.update { it.copy(genuineDesire = value.coerceIn(0, 100)) }
    }

    fun onEventTypeChanged(eventType: String?) {
        _currentEntry.update { it.copy(eventType = eventType) }
    }

    fun onIntensityChanged(value: Int) {
        _currentEntry.update { it.copy(intensity = value.coerceIn(1, 5)) }
    }

    fun onNotesChanged(notes: String) {
        _currentEntry.update { it.copy(notes = notes) }
    }

    fun onToggleEmotion(emotionId: String) {
        _currentEntry.update { entry ->
            val current = entry.emotions
            val newEmotions = if (current.contains(emotionId)) {
                current.filterNot { it == emotionId }
            } else {
                current + emotionId
            }
            entry.copy(emotions = newEmotions)
        }
    }

    fun onEdit(entry: RelationshipEntry) {
        _currentEntry.value = entry
        _isEditing.value = true
    }

    fun onCancelEdit() {
        _currentEntry.value = RelationshipEntry()
        _isEditing.value = false
    }

    fun onSaveEntry() {
        val entry = _currentEntry.value
        viewModelScope.launch {
            try {
                if (_isEditing.value) {
                    repository.updateEntry(entry)
                } else {
                    repository.addEntry(entry.copy(createdAt = OffsetDateTime.now()))
                }
                _currentEntry.value = RelationshipEntry()
                _isEditing.value = false
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "No se pudo guardar la entrada")
                }
            }
        }
    }

    fun onDeleteEntry(entry: RelationshipEntry) {
        viewModelScope.launch {
            try {
                repository.deleteEntry(entry.id)
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "No se pudo eliminar la entrada")
                }
            }
        }
    }

    fun importEntries(entries: List<RelationshipEntry>) {
        viewModelScope.launch {
            try {
                repository.importEntries(entries)
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "No se pudieron importar las entradas")
                }
            }
        }
    }

    data class DiaryUiState(
        val entries: List<RelationshipEntry> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SereneApplication)
                DiaryViewModel(application.container.diaryRepository)
            }
        }
    }
}
