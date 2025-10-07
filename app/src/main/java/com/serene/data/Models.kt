package com.serene.data

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

enum class EntryType { ACTIVITY, EVENT }

data class Emotion(
    val id: String,
    val label: String,
    val icon: String,
    val colorHex: Long
)

data class EventCategory(
    val id: String,
    val label: String,
    val emoji: String,
    val colorHex: Long
)

data class RelationshipEntry(
    val id: String = UUID.randomUUID().toString(),
    val type: EntryType = EntryType.ACTIVITY,
    val date: LocalDate = LocalDate.now(),
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val activity: String = "",
    val genuineDesire: Int = 50,
    val emotions: List<String> = emptyList(),
    val eventType: String? = null,
    val intensity: Int = 3,
    val notes: String = ""
) {
    val formattedTimestamp: String
        get() = createdAt.format(DateTimeFormatter.ofPattern("dd 'de' MMMM yyyy • HH:mm"))
}
