package com.serene.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serene.data.DiaryRepository
import com.serene.data.EntryType
import com.serene.data.RelationshipEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime

class DiaryViewModel(
    private val repository: DiaryRepository = DiaryRepository()
) : ViewModel() {

    private val _currentEntry = MutableStateFlow(RelationshipEntry())
    val currentEntry: StateFlow<RelationshipEntry> = _currentEntry

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    val entries = repository.entries

    init {
        viewModelScope.launch {
            repository.loadInitialEntries()
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
            if (_isEditing.value) {
                repository.updateEntry(entry)
            } else {
                repository.addEntry(entry.copy(createdAt = OffsetDateTime.now()))
            }
            _currentEntry.value = RelationshipEntry()
            _isEditing.value = false
        }
    }

    fun onDeleteEntry(entry: RelationshipEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry.id)
        }
    }

    fun importEntries(entries: List<RelationshipEntry>) {
        viewModelScope.launch {
            entries.forEach { repository.addEntry(it) }
        }
    }
}
