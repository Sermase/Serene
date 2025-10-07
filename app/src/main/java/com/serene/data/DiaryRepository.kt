package com.serene.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DiaryRepository {
    private val _entries = MutableStateFlow<List<RelationshipEntry>>(emptyList())
    val entries: StateFlow<List<RelationshipEntry>> = _entries

    suspend fun addEntry(entry: RelationshipEntry) {
        // TODO: Replace with Firebase persistence.
        delay(150)
        _entries.value = _entries.value + entry
    }

    suspend fun updateEntry(updated: RelationshipEntry) {
        delay(150)
        _entries.value = _entries.value.map { if (it.id == updated.id) updated else it }
    }

    suspend fun deleteEntry(id: String) {
        delay(150)
        _entries.value = _entries.value.filterNot { it.id == id }
    }

    suspend fun loadInitialEntries() {
        delay(300)
        _entries.value = emptyList()
    }
}
