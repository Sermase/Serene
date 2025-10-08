package com.serene.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class InMemoryDiaryDataSource : DiaryDataSource {
    private val _entries = MutableStateFlow<List<RelationshipEntry>>(emptyList())
    override val entries: StateFlow<List<RelationshipEntry>> = _entries

    override suspend fun addEntry(entry: RelationshipEntry) {
        delay(120)
        _entries.update { current -> current + entry }
    }

    override suspend fun updateEntry(entry: RelationshipEntry) {
        delay(120)
        _entries.update { current -> current.map { if (it.id == entry.id) entry else it } }
    }

    override suspend fun deleteEntry(id: String) {
        delay(120)
        _entries.update { current -> current.filterNot { it.id == id } }
    }

    override suspend fun importEntries(entries: List<RelationshipEntry>) {
        if (entries.isEmpty()) return
        delay(180)
        val incomingById = entries.associateBy { it.id }
        _entries.update { current ->
            current.filterNot { incomingById.containsKey(it.id) } + entries
        }
    }

    override suspend fun refresh() {
        delay(150)
        // In memoria no hay una fuente externa, así que mantenemos el estado actual.
    }
}
