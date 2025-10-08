package com.serene.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Implementación de [DiaryDataSource] respaldada por Firebase Firestore.
 *
 * No se registra automáticamente en el contenedor de la aplicación para evitar fallos en entornos
 * sin Firebase configurado. Cuando añadas `google-services.json`, crea una instancia de este data
 * source y pásalo al [DiaryRepository].
 */
class FirebaseDiaryDataSource(
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DiaryDataSource {

    private val collection = firestore.collection(COLLECTION_NAME)
    private val _entries = MutableStateFlow<List<RelationshipEntry>>(emptyList())
    override val entries: StateFlow<List<RelationshipEntry>> = _entries

    private var listenerRegistration: ListenerRegistration? = null

    init {
        listenForUpdates()
    }

    override suspend fun addEntry(entry: RelationshipEntry) {
        withContext(ioDispatcher) {
            collection.document(entry.id).set(entry.toFirestoreMap()).await()
        }
    }

    override suspend fun updateEntry(entry: RelationshipEntry) {
        withContext(ioDispatcher) {
            collection.document(entry.id).set(entry.toFirestoreMap()).await()
        }
    }

    override suspend fun deleteEntry(id: String) {
        withContext(ioDispatcher) {
            collection.document(id).delete().await()
        }
    }

    override suspend fun importEntries(entries: List<RelationshipEntry>) {
        if (entries.isEmpty()) return
        withContext(ioDispatcher) {
            firestore.runBatch { batch ->
                entries.forEach { entry ->
                    batch.set(collection.document(entry.id), entry.toFirestoreMap())
                }
            }.await()
        }
    }

    override suspend fun refresh() {
        withContext(ioDispatcher) {
            // Forzamos una recarga puntual solicitando los documentos de nuevo.
            val snapshot = collection.orderBy("createdAt", Query.Direction.DESCENDING).get().await()
            _entries.value = snapshot.documents.mapNotNull { it.toRelationshipEntry() }
        }
    }

    private fun listenForUpdates() {
        listenerRegistration?.remove()
        listenerRegistration = collection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                _entries.value = snapshot?.documents?.mapNotNull { document ->
                    document.toRelationshipEntry()
                } ?: emptyList()
            }
    }

    fun clearListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    private fun RelationshipEntry.toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "type" to type.name,
        "date" to date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        "createdAt" to createdAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        "activity" to activity,
        "genuineDesire" to genuineDesire,
        "emotions" to emotions,
        "eventType" to eventType,
        "intensity" to intensity,
        "notes" to notes
    )

    private fun Map<String, Any?>.toRelationshipEntry(): RelationshipEntry? {
        val id = this["id"] as? String ?: return null
        val typeName = this["type"] as? String ?: EntryType.ACTIVITY.name
        val date = (this["date"] as? String)?.let(LocalDate::parse) ?: LocalDate.now()
        val createdAt = (this["createdAt"] as? String)?.let(OffsetDateTime::parse)
            ?: OffsetDateTime.now()
        val activity = this["activity"] as? String ?: ""
        val genuineDesire = (this["genuineDesire"] as? Long)?.toInt()
            ?: (this["genuineDesire"] as? Int) ?: 50
        val emotions = (this["emotions"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val eventType = this["eventType"] as? String
        val intensity = (this["intensity"] as? Long)?.toInt()
            ?: (this["intensity"] as? Int) ?: 3
        val notes = this["notes"] as? String ?: ""

        return RelationshipEntry(
            id = id,
            type = runCatching { EntryType.valueOf(typeName) }.getOrDefault(EntryType.ACTIVITY),
            date = date,
            createdAt = createdAt,
            activity = activity,
            genuineDesire = genuineDesire,
            emotions = emotions,
            eventType = eventType,
            intensity = intensity,
            notes = notes
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toRelationshipEntry(): RelationshipEntry? {
        val data = data ?: return null
        return data.toRelationshipEntry()
    }

    companion object {
        private const val COLLECTION_NAME = "diary_entries"
    }
}
