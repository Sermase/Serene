package com.serene.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serene.app.ui.theme.BlushPetal
import com.serene.app.ui.theme.GentleSky
import com.serene.app.ui.theme.LavenderMist
import com.serene.app.ui.theme.MintWhisper
import com.serene.app.ui.theme.SereneTheme
import com.serene.app.ui.theme.SurfaceMuted
import com.serene.app.ui.theme.TextPrimary
import com.serene.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SereneApp()
        }
    }
}

enum class EntryType { Activity, Event }

data class Emotion(
    val id: String,
    val label: String,
    val color: Color
)

data class EventType(
    val id: String,
    val label: String,
    val color: Color
)

data class DiaryEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val type: EntryType,
    val activity: String? = null,
    val genuineDesire: Int = 50,
    val emotions: List<String> = emptyList(),
    val eventType: String? = null,
    val intensity: Int = 3,
    val notes: String = "",
    val createdAt: String = DateTimeFormatter.ISO_DATE_TIME.format(java.time.OffsetDateTime.now())
)

private val availableEmotions = listOf(
    Emotion("happy", "😊 Feliz", Color(0xFF10B981)),
    Emotion("love", "😍 Enamorado/a", Color(0xFFEC4899)),
    Emotion("calm", "😌 Tranquilo/a", Color(0xFF06B6D4)),
    Emotion("excited", "🤩 Emocionado/a", Color(0xFFF59E0B)),
    Emotion("grateful", "🙏 Agradecido/a", Color(0xFF8B5CF6)),
    Emotion("neutral", "😐 Neutral", Color(0xFF6B7280)),
    Emotion("tired", "😴 Cansado/a", Color(0xFF9CA3AF)),
    Emotion("confused", "😕 Confundido/a", Color(0xFFF97316)),
    Emotion("pressured", "😰 Presionado/a", Color(0xFFEAB308)),
    Emotion("sad", "😢 Triste", Color(0xFF3B82F6)),
    Emotion("frustrated", "😤 Frustrado/a", Color(0xFFEF4444)),
    Emotion("angry", "😠 Enojado/a", Color(0xFFDC2626))
)

private val eventTypes = listOf(
    EventType("discussion", "💬 Conversación profunda", Color(0xFF60A5FA)),
    EventType("argument", "🗯️ Conflicto", Color(0xFFF87171)),
    EventType("reproach", "😔 Reproche", Color(0xFFF97316)),
    EventType("trip", "✈️ Escapada", Color(0xFF34D399)),
    EventType("celebration", "🎉 Celebración", Color(0xFFA855F7)),
    EventType("compliment", "💖 Palabras bonitas", Color(0xFFF472B6)),
    EventType("support", "🤗 Apoyo mutuo", Color(0xFF0EA5E9)),
    EventType("surprise", "🎁 Sorpresa", Color(0xFFFBBF24)),
    EventType("intimacy", "💕 Momento íntimo", Color(0xFFDB2777)),
    EventType("milestone", "🏆 Hito importante", Color(0xFF059669))
)

@Composable
fun SereneApp() {
    val sereneLocale = remember { Locale("es", "ES") }
    SereneTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val entries = remember { mutableStateListOf<DiaryEntry>() }
        var editingEntryId by rememberSaveable { mutableStateOf<String?>(null) }
        var selectedTab by rememberSaveable { mutableStateOf(0) }

        val currentEntryState = remember { mutableStateOf(createDefaultEntry()) }

        LaunchedEffect(editingEntryId) {
            val editingEntry = entries.find { it.id == editingEntryId }
            currentEntryState.value = editingEntry ?: createDefaultEntry()
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Serene",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Un diario sensible para nutrir tu relación",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    LavenderMist.copy(alpha = 0.45f),
                                    BlushPetal.copy(alpha = 0.35f),
                                    GentleSky.copy(alpha = 0.45f)
                                )
                            )
                        )
                        .padding(innerPadding)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = TextPrimary,
                        indicator = { tabPositions ->
                            val currentTabPosition = tabPositions[selectedTab]
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(currentTabPosition)
                                    .height(3.dp)
                                    .background(LavenderMist, RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                            )
                        }
                    ) {
                        val tabs = listOf("Registrar", "Estadísticas", "Historial")
                        tabs.forEachIndexed { index, label ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (index) {
                                                0 -> Icons.Default.NoteAlt
                                                1 -> Icons.Default.Insights
                                                else -> Icons.Default.History
                                            },
                                            contentDescription = null,
                                            tint = if (selectedTab == index) TextPrimary else TextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = label)
                                    }
                                }
                            )
                        }
                    }
                    AnimatedVisibility(visible = selectedTab == 0, enter = fadeIn(), exit = fadeOut()) {
                        EntryForm(
                            currentEntry = currentEntryState.value,
                            onEntryChange = { currentEntryState.value = it },
                            onReset = {
                                editingEntryId = null
                                currentEntryState.value = createDefaultEntry()
                            },
                            onSubmit = { entry ->
                                if (!entry.isValid()) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Revisa la información de la entrada")
                                    }
                                    return@EntryForm
                                }

                                val updatedEntry = entry.copy(
                                    id = editingEntryId ?: UUID.randomUUID().toString()
                                )
                                if (editingEntryId == null) {
                                    entries.add(updatedEntry)
                                } else {
                                    val index = entries.indexOfFirst { it.id == editingEntryId }
                                    if (index != -1) {
                                        entries[index] = updatedEntry
                                    }
                                }

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Entrada guardada con cariño")
                                }

                                editingEntryId = null
                                currentEntryState.value = createDefaultEntry()

                                // Placeholder for Firebase persistence
                                // saveEntryToFirebase(updatedEntry)
                            },
                            locale = sereneLocale
                        )
                    }
                    AnimatedVisibility(visible = selectedTab == 1, enter = fadeIn(), exit = fadeOut()) {
                        StatisticsView(entries = entries)
                    }
                    AnimatedVisibility(visible = selectedTab == 2, enter = fadeIn(), exit = fadeOut()) {
                        HistoryView(
                            entries = entries,
                            onEdit = { entry ->
                                editingEntryId = entry.id
                                selectedTab = 0
                            },
                            onDelete = { entry ->
                                entries.remove(entry)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Entrada eliminada suavemente")
                                }
                                // Placeholder for Firebase delete
                            },
                            locale = sereneLocale
                        )
                    }
                }
            }
        }
    }
}

private fun createDefaultEntry(): DiaryEntry = DiaryEntry(
    date = LocalDate.now(),
    type = EntryType.Activity,
    genuineDesire = 60,
    intensity = 3
)

private fun DiaryEntry.isValid(): Boolean {
    return when (type) {
        EntryType.Activity -> !activity.isNullOrBlank()
        EntryType.Event -> !eventType.isNullOrBlank()
    }
}

@Composable
private fun EntryForm(
    currentEntry: DiaryEntry,
    onEntryChange: (DiaryEntry) -> Unit,
    onReset: () -> Unit,
    onSubmit: (DiaryEntry) -> Unit,
    locale: Locale
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = SurfaceMuted.copy(alpha = 0.7f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "¿Qué deseas registrar hoy?",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ChoiceChip(
                        selected = currentEntry.type == EntryType.Activity,
                        label = "Actividad juntos",
                        onClick = {
                            onEntryChange(
                                currentEntry.copy(
                                    type = EntryType.Activity,
                                    eventType = null,
                                    intensity = 3
                                )
                            )
                        },
                        icon = Icons.Default.Favorite
                    )
                    ChoiceChip(
                        selected = currentEntry.type == EntryType.Event,
                        label = "Evento de la relación",
                        onClick = {
                            onEntryChange(
                                currentEntry.copy(
                                    type = EntryType.Event,
                                    activity = null,
                                    genuineDesire = 50
                                )
                            )
                        },
                        icon = Icons.Default.CalendarMonth
                    )
                }

                DateField(
                    date = currentEntry.date,
                    onDateChange = { onEntryChange(currentEntry.copy(date = it)) },
                    locale = locale
                )

                when (currentEntry.type) {
                    EntryType.Activity -> ActivitySection(currentEntry, onEntryChange)
                    EntryType.Event -> EventSection(currentEntry, onEntryChange)
                }

                EmotionSelector(
                    selectedEmotions = currentEntry.emotions,
                    onEmotionToggled = { id ->
                        val updated = currentEntry.emotions.toMutableList().apply {
                            if (contains(id)) remove(id) else add(id)
                        }
                        onEntryChange(currentEntry.copy(emotions = updated))
                    }
                )

                OutlinedTextField(
                    value = currentEntry.notes,
                    onValueChange = { onEntryChange(currentEntry.copy(notes = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Reflexiones suaves") },
                    placeholder = { Text("¿Qué resonó en tu corazón?") },
                    minLines = 3
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onReset,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Limpiar")
                    }
                    TextButton(onClick = { onSubmit(currentEntry) }) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar con cariño")
                    }
                }

                Divider(color = TextSecondary.copy(alpha = 0.2f))

                FirebaseReminder()
            }
        }
    }
}

@Composable
private fun ChoiceChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else TextSecondary
            )
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun DateField(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    locale: Locale
) {
    val formatted = remember(date, locale) {
        date.format(DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", locale))
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Fecha", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
        OutlinedButton(onClick = { onDateChange(LocalDate.now()) }) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(formatted)
        }
        Text(
            text = "(Toca para volver a hoy)",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun ActivitySection(currentEntry: DiaryEntry, onEntryChange: (DiaryEntry) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = currentEntry.activity.orEmpty(),
            onValueChange = { onEntryChange(currentEntry.copy(activity = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Actividad realizada") },
            placeholder = { Text("Una cena tranquila, un paseo, una conversación...") }
        )
        Text(
            text = "¿Cuánto nació del deseo genuino? ${currentEntry.genuineDesire}%",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFEE2E2), Color(0xFFFDE68A), Color(0xFFBBF7D0))
                    )
                )
                .padding(16.dp)
        ) {
            Slider(
                value = currentEntry.genuineDesire.toFloat(),
                onValueChange = {
                    onEntryChange(currentEntry.copy(genuineDesire = it.toInt()))
                },
                valueRange = 0f..100f
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Obligación", style = MaterialTheme.typography.bodySmall)
                Text("Deseo genuino", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun EventSection(currentEntry: DiaryEntry, onEntryChange: (DiaryEntry) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Tipo de evento", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
        FlowRowSpacing {
            eventTypes.forEach { type ->
                ChoiceChip(
                    selected = currentEntry.eventType == type.id,
                    label = type.label,
                    onClick = { onEntryChange(currentEntry.copy(eventType = type.id)) },
                    icon = Icons.Default.Favorite
                )
            }
        }
        Text(text = "Intensidad", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { level ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (level <= currentEntry.intensity) LavenderMist else Color.White)
                        .border(1.dp, LavenderMist.copy(alpha = 0.5f), CircleShape)
                        .clickable { onEntryChange(currentEntry.copy(intensity = level)) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$level", color = TextPrimary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun EmotionSelector(
    selectedEmotions: List<String>,
    onEmotionToggled: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "¿Cómo te sentiste?",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )
        FlowRowSpacing {
            availableEmotions.forEach { emotion ->
                val selected = emotion.id in selectedEmotions
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (selected) emotion.color.copy(alpha = 0.9f) else Color.White)
                        .border(1.dp, emotion.color.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .clickable { onEmotionToggled(emotion.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = emotion.label,
                        color = if (selected) Color.White else TextSecondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowSpacing(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun FirebaseReminder() {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = GentleSky.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TextPrimary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Sincronización con Firebase", fontWeight = FontWeight.Medium)
            }
            Text(
                text = "El proyecto ya está listo para conectarse con Firebase Firestore. Revisa la guía en README.md para completar la configuración cuando sea el momento.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StatisticsView(entries: List<DiaryEntry>) {
    val activities = entries.filter { it.type == EntryType.Activity }
    val events = entries.filter { it.type == EntryType.Event }
    val averageDesire = if (activities.isNotEmpty()) activities.map { it.genuineDesire }.average().toInt() else 0
    val recentTrend = if (activities.size > 3) {
        val recent = activities.takeLast(3).map { it.genuineDesire }.average()
        val previous = activities.take((activities.size - 3).coerceAtLeast(1)).map { it.genuineDesire }.average()
        (recent - previous).toInt()
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryCard(
                title = "Deseo Genuino",
                value = "$averageDesire%",
                description = "promedio",
                accent = MintWhisper
            )
            SummaryCard(
                title = "Actividades",
                value = "${activities.size}",
                description = "registradas",
                accent = LavenderMist
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryCard(
                title = "Eventos",
                value = "${events.size}",
                description = "memorables",
                accent = GentleSky
            )
            SummaryCard(
                title = "Tendencia",
                value = if (recentTrend > 0) "+$recentTrend%" else "$recentTrend%",
                description = "últimas semanas",
                accent = BlushPetal
            )
        }

        if (activities.isNotEmpty()) {
            DesireSparkline(activities.map { it.genuineDesire })
        }

        if (entries.any { it.emotions.isNotEmpty() }) {
            EmotionFrequency(entries)
        }

        if (events.isNotEmpty()) {
            EventDistribution(events)
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, description: String, accent: Color) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(text = value, style = MaterialTheme.typography.displayLarge, color = TextPrimary)
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun DesireSparkline(values: List<Int>) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = SurfaceMuted.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Insights, contentDescription = null, tint = TextPrimary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Evolución del deseo genuino", style = MaterialTheme.typography.titleMedium)
            }
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
            ) {
                if (values.size < 2) return@Canvas
                val maxValue = 100f
                val minValue = 0f
                val stepX = size.width / (values.size - 1)
                val pathColor = LavenderMist

                for (i in 0 until values.size - 1) {
                    val startX = stepX * i
                    val startY = size.height - ((values[i] - minValue) / (maxValue - minValue) * size.height)
                    val endX = stepX * (i + 1)
                    val endY = size.height - ((values[i + 1] - minValue) / (maxValue - minValue) * size.height)
                    drawLine(
                        color = pathColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionFrequency(entries: List<DiaryEntry>) {
    val frequency = remember(entries) {
        buildMap<String, Int> {
            entries.flatMap { it.emotions }.forEach { emotion ->
                merge(emotion, 1, Int::plus)
            }
        }
    }

    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = SurfaceMuted.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = TextPrimary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Emociones recurrentes", style = MaterialTheme.typography.titleMedium)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                frequency.entries
                    .sortedByDescending { it.value }
                    .forEach { (emotionId, count) ->
                        val emotion = availableEmotions.find { it.id == emotionId } ?: return@forEach
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(emotion.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = emotion.label,
                                modifier = Modifier.weight(1f),
                                color = TextPrimary
                            )
                            Text(text = count.toString(), color = TextSecondary)
                        }
                    }
            }
        }
    }
}

@Composable
private fun EventDistribution(events: List<DiaryEntry>) {
    val grouping = remember(events) {
        buildMap<String, Int> {
            events.mapNotNull { it.eventType }.forEach { id ->
                merge(id, 1, Int::plus)
            }
        }
    }

    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = SurfaceMuted.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = TextPrimary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Momentos destacados", style = MaterialTheme.typography.titleMedium)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                grouping.entries.sortedByDescending { it.value }.forEach { (typeId, count) ->
                    val eventType = eventTypes.find { it.id == typeId } ?: return@forEach
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(eventType.color.copy(alpha = 0.15f))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = eventType.label, modifier = Modifier.weight(1f), color = TextPrimary)
                        Text(text = "$count", color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryView(
    entries: List<DiaryEntry>,
    onEdit: (DiaryEntry) -> Unit,
    onDelete: (DiaryEntry) -> Unit,
    locale: Locale
) {
    if (entries.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.height(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("Tu historia está en blanco", style = MaterialTheme.typography.titleMedium)
            Text("Añade la primera entrada para comenzar a recordar", color = TextSecondary)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(entries.sortedByDescending { it.date }) { entry ->
                HistoryCard(entry = entry, onEdit = onEdit, onDelete = onDelete, locale = locale)
            }
        }
    }
}

@Composable
private fun HistoryCard(
    entry: DiaryEntry,
    onEdit: (DiaryEntry) -> Unit,
    onDelete: (DiaryEntry) -> Unit,
    locale: Locale
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = SurfaceMuted.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = entry.date.format(DateTimeFormatter.ofPattern("EEEE, dd MMM", locale)),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = entrySummary(entry),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { onEdit(entry) }) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = TextSecondary)
                    }
                    IconButton(onClick = { onDelete(entry) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = TextSecondary)
                    }
                }
            }
            if (entry.type == EntryType.Activity) {
                DesireBadge(entry.genuineDesire)
            } else {
                IntensityDots(intensity = entry.intensity)
            }
            if (entry.emotions.isNotEmpty()) {
                FlowRowSpacing {
                    entry.emotions.forEach { id ->
                        val emotion = availableEmotions.find { it.id == id } ?: return@forEach
                        Text(
                            text = emotion.label,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(emotion.color.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            color = TextPrimary
                        )
                    }
                }
            }
            if (entry.notes.isNotBlank()) {
                Text(
                    text = entry.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun DesireBadge(value: Int) {
    val color = when {
        value >= 70 -> Color(0xFF10B981)
        value >= 40 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "Deseo genuino $value%", color = color)
        }
    }
}

@Composable
private fun IntensityDots(intensity: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        (1..5).forEach { index ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (index <= intensity) LavenderMist else Color.LightGray)
            )
        }
    }
}

private fun entrySummary(entry: DiaryEntry): String {
    return when (entry.type) {
        EntryType.Activity -> entry.activity.orEmpty()
        EntryType.Event -> eventTypes.find { it.id == entry.eventType }?.label ?: "Evento"
    }
}

// Placeholder Firebase integration utilities
// fun initializeFirebase(context: Context) { ... }
// suspend fun saveEntryToFirebase(entry: DiaryEntry) { ... }
// suspend fun loadEntriesFromFirebase(): List<DiaryEntry> { ... }
