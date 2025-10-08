package com.serene.data

import kotlinx.coroutines.flow.StateFlow

interface DiaryDataSource {
    val entries: StateFlow<List<RelationshipEntry>>

    suspend fun addEntry(entry: RelationshipEntry)

    suspend fun updateEntry(entry: RelationshipEntry)

    suspend fun deleteEntry(id: String)

    suspend fun importEntries(entries: List<RelationshipEntry>)

    suspend fun refresh()
}
