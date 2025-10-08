package com.serene.data

class DiaryRepository(
    private val dataSource: DiaryDataSource = InMemoryDiaryDataSource()
) {
    val entries = dataSource.entries

    suspend fun addEntry(entry: RelationshipEntry) {
        dataSource.addEntry(entry)
    }

    suspend fun updateEntry(entry: RelationshipEntry) {
        dataSource.updateEntry(entry)
    }

    suspend fun deleteEntry(id: String) {
        dataSource.deleteEntry(id)
    }

    suspend fun importEntries(entries: List<RelationshipEntry>) {
        dataSource.importEntries(entries)
    }

    suspend fun refreshEntries() {
        dataSource.refresh()
    }
}
