package com.serene.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serene.data.Emotion
import com.serene.data.EntryType
import com.serene.data.EventCategory
import com.serene.data.RelationshipEntry
import com.serene.ui.theme.SereneTheme
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

private val pastelGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFE3F2FD),
        Color(0xFFFCE4EC),
        Color(0xFFF3E5F5)
    )
)

private val availableEmotions = listOf(
    Emotion("happy", "Feliz", "😊", 0xFF4CAF50),
    Emotion("love", "Enamorado/a", "😍", 0xFFF06292),
    Emotion("calm", "Sereno/a", "😌", 0xFF4DD0E1),
    Emotion("excited", "Entusiasmado/a", "🤩", 0xFFFFB74D),
    Emotion("grateful", "Agradecido/a", "🙏", 0xFF9575CD),
    Emotion("neutral", "En calma", "😐", 0xFF90A4AE),
    Emotion("tired", "Cansado/a", "😴", 0xFFB0BEC5),
    Emotion("confused", "Confundido/a", "😕", 0xFFFFA726),
    Emotion("pressured", "Presionado/a", "😰", 0xFFFFD54F),
    Emotion("sad", "Melancólico/a", "😢", 0xFF64B5F6),
    Emotion("frustrated", "Frustrado/a", "😤", 0xFFE57373),
    Emotion("angry", "Molesto/a", "😠", 0xFFE53935)
)

private val eventCategories = listOf(
    EventCategory("discussion", "Conversación profunda", "💬", 0xFFE57373),
    EventCategory("argument", "Conflicto", "🗯️", 0xFFD32F2F),
    EventCategory("reproach", "Reproche", "😔", 0xFFFFA726),
    EventCategory("trip", "Escapada", "✈️", 0xFF26A69A),
    EventCategory("celebration", "Celebración", "🎉", 0xFF9575CD),
    EventCategory("compliment", "Palabras bonitas", "💖", 0xFFF06292),
    EventCategory("support", "Apoyo mutuo", "🤗", 0xFF4DD0E1),
    EventCategory("surprise", "Sorpresa", "🎁", 0xFFFFB74D),
    EventCategory("intimacy", "Intimidad", "💕", 0xFFD81B60),
    EventCategory("milestone", "Hito", "🏆", 0xFF2E7D32)
)

@Composable
fun SereneApp(viewModel: DiaryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    SereneTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DiaryScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun DiaryScreen(viewModel: DiaryViewModel) {
    val entries by viewModel.entries.collectAsState()
    val currentEntry by viewModel.currentEntry.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf<RelationshipEntry?>(null) }
    var showImportSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { selectedTab = 0 },
                containerColor = Color(0xFFBA68C8),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null)
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier
            .background(pastelGradient)
            .fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(pastelGradient)
        ) {
            Header(onExport = {
                exportEntries(context.cacheDir, entries)
            }, onImport = {
                showImportSheet = true
            })

            Spacer(modifier = Modifier.height(12.dp))

            val tabs = listOf("Nueva entrada", "Estadísticas", "Historia")
            TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> EntryForm(
                    entry = currentEntry,
                    isEditing = isEditing,
                    onTypeSelected = viewModel::onEntryTypeSelected,
                    onDateChanged = viewModel::onDateChanged,
                    onActivityChanged = viewModel::onActivityChanged,
                    onGenuineDesireChanged = viewModel::onGenuineDesireChanged,
                    onEventTypeChanged = viewModel::onEventTypeChanged,
                    onIntensityChanged = viewModel::onIntensityChanged,
                    onNotesChanged = viewModel::onNotesChanged,
                    onToggleEmotion = viewModel::onToggleEmotion,
                    onSave = viewModel::onSaveEntry,
                    onCancel = viewModel::onCancelEdit
                )

                1 -> StatsView(entries = entries)
                2 -> HistoryView(entries = entries, onEdit = {
                    viewModel.onEdit(it)
                    selectedTab = 0
                }, onDelete = { showDeleteDialog = it })
            }
        }
    }

    if (showDeleteDialog != null) {
        val entry = showDeleteDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("¿Eliminar entrada?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDeleteEntry(entry)
                    showDeleteDialog = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showImportSheet) {
        ImportSheet(
            sheetState = sheetState,
            onDismiss = { showImportSheet = false },
            onImport = { imported ->
                coroutineScope.launch {
                    viewModel.importEntries(imported)
                }
            }
        )
    }
}

@Composable
private fun Header(onExport: () -> Unit, onImport: () -> Unit) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Serene",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF6A1B9A)
                )
                Text(
                    text = "Diario de conexión y calma",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7B1FA2)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onExport) {
                    Icon(Icons.Default.Download, contentDescription = "Exportar")
                }
                IconButton(onClick = onImport) {
                    Icon(Icons.Default.Upload, contentDescription = "Importar")
                }
            }
        }
    }
}

@Composable
private fun EntryForm(
    entry: RelationshipEntry,
    isEditing: Boolean,
    onTypeSelected: (EntryType) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onActivityChanged: (String) -> Unit,
    onGenuineDesireChanged: (Int) -> Unit,
    onEventTypeChanged: (String?) -> Unit,
    onIntensityChanged: (Int) -> Unit,
    onNotesChanged: (String) -> Unit,
    onToggleEmotion: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (isEditing) "Edita tus recuerdos" else "Cuéntame tu momento",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF6A1B9A)
            )
        }
        item {
            TypeSelector(selectedType = entry.type, onTypeSelected = onTypeSelected)
        }
        item {
            DatePickerCard(date = entry.date, onDateChanged = onDateChanged)
        }
        item {
            AnimatedVisibility(visible = entry.type == EntryType.ACTIVITY) {
                ActivityCard(activity = entry.activity, onChange = onActivityChanged)
            }
        }
        item {
            AnimatedVisibility(visible = entry.type == EntryType.EVENT) {
                EventCategoryCard(selected = entry.eventType, onChange = onEventTypeChanged)
            }
        }
        item {
            if (entry.type == EntryType.ACTIVITY) {
                DesireSlider(value = entry.genuineDesire, onValueChange = onGenuineDesireChanged)
            } else {
                IntensitySelector(value = entry.intensity, onValueChange = onIntensityChanged)
            }
        }
        item {
            EmotionSelector(selected = entry.emotions, onToggleEmotion = onToggleEmotion)
        }
        item {
            NotesCard(notes = entry.notes, onNotesChanged = onNotesChanged)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (isEditing) "Actualizar" else "Guardar")
                }
                AnimatedVisibility(isEditing) {
                    TextButton(onClick = onCancel) {
                        Text("Cancelar")
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun TypeSelector(selectedType: EntryType, onTypeSelected: (EntryType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        EntryType.values().forEach { type ->
            val isSelected = type == selectedType
            val gradient = if (isSelected) Brush.linearGradient(
                listOf(Color(0xFFC158DC), Color(0xFF9575CD))
            ) else Brush.linearGradient(listOf(Color.White, Color.White))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(gradient)
                    .clickable {
                        onTypeSelected(type)
                    }
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (type == EntryType.ACTIVITY) Icons.Default.Favorite else Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else Color(0xFF6A1B9A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (type == EntryType.ACTIVITY) "Actividad" else "Evento",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) Color.White else Color(0xFF6A1B9A)
                    )
                }
            }
        }
    }
}

@Composable
private fun DatePickerCard(date: LocalDate, onDateChanged: (LocalDate) -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM yyyy")
    var expanded by remember { mutableStateOf(false) }
    val calendarInteraction = remember { MutableInteractionSource() }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Fecha", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = date.format(formatter),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6A1B9A),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3E5F5))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(interactionSource = calendarInteraction, indication = null) {
                        expanded = !expanded
                    }
            )
            AnimatedVisibility(visible = expanded, enter = fadeIn(), exit = fadeOut()) {
                SerenityCalendar(selectedDate = date, onDateSelected = {
                    onDateChanged(it)
                    expanded = false
                })
            }
        }
    }
}

@Composable
private fun ActivityCard(activity: String, onChange: (String) -> Unit) {
    SerenityTextField(
        title = "Actividad compartida",
        value = activity,
        placeholder = "Ej: Paseo al atardecer",
        onValueChange = onChange
    )
}

@Composable
private fun EventCategoryCard(selected: String?, onChange: (String?) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Tipo de evento", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(220.dp)
            ) {
                items(eventCategories) { category ->
                    val isSelected = category.id == selected
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(category.colorHex.toInt()) else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .clickable { onChange(category.id) }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(category.emoji, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                category.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) Color.White else Color(0xFF6A1B9A)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesireSlider(value: Int, onValueChange: (Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Deseo genuino", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            val animatedProgress by animateFloatAsState(targetValue = value / 100f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8BBD0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(14.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(
                            listOf(Color(0xFFCE93D8), Color(0xFF8E24AA))
                        ))
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Obligación")
                Text("${value}%")
                Text("Deseo")
            }
            SerenitySlider(value = value, onValueChange = onValueChange)
        }
    }
}

@Composable
private fun IntensitySelector(value: Int, onValueChange: (Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Intensidad del evento", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                (1..5).forEach { level ->
                    val selected = level <= value
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (selected) Color(0xFFAB47BC) else Color(0xFFF3E5F5))
                            .clickable { onValueChange(level) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(level.toString(), color = if (selected) Color.White else Color(0xFF6A1B9A))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmotionSelector(selected: List<String>, onToggleEmotion: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Emociones presentes", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(260.dp)
            ) {
                items(availableEmotions) { emotion ->
                    val isSelected = selected.contains(emotion.id)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(emotion.colorHex.toInt()) else Color.White
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .clickable { onToggleEmotion(emotion.id) }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(emotion.icon, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = emotion.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) Color.White else Color(0xFF6A1B9A)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesCard(notes: String, onNotesChanged: (String) -> Unit) {
    SerenityTextField(
        title = "Reflexiones",
        value = notes,
        placeholder = "¿Qué aprendiste de este momento?",
        onValueChange = onNotesChanged,
        maxLines = 4
    )
}

@Composable
private fun StatsView(entries: List<RelationshipEntry>) {
    if (entries.isEmpty()) {
        EmptyState(icon = Icons.Default.InsertChart, message = "Aún no hay datos para mostrar")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryRow(entries)
        }
        item {
            DesireEvolution(entries)
        }
        item {
            EmotionSummary(entries)
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun SummaryRow(entries: List<RelationshipEntry>) {
    val activities = entries.filter { it.type == EntryType.ACTIVITY }
    val events = entries.filter { it.type == EntryType.EVENT }
    val averageDesire = if (activities.isNotEmpty()) activities.map { it.genuineDesire }.average() else 0.0
    val trend = calculateTrend(activities)

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        SummaryCard(title = "Deseo medio", value = "${averageDesire.toInt()}%", icon = Icons.Default.Favorite)
        SummaryCard(title = "Actividades", value = activities.size.toString(), icon = Icons.Default.CalendarMonth)
        SummaryCard(title = "Eventos", value = events.size.toString(), icon = Icons.Default.History)
        SummaryCard(title = "Tendencia", value = (if (trend >= 0) "+" else "") + trend + "%", icon = Icons.Default.InsertChart)
    }
}

@Composable
private fun SummaryCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.weight(1f)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = Color(0xFF7B1FA2))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = Color(0xFF6A1B9A))
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DesireEvolution(entries: List<RelationshipEntry>) {
    val activities = entries.filter { it.type == EntryType.ACTIVITY }
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Evolución del deseo", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            if (activities.isEmpty()) {
                Text("Registra actividades para ver esta gráfica")
            } else {
                DesireLineChart(activities)
            }
        }
    }
}

@Composable
private fun DesireLineChart(activities: List<RelationshipEntry>) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM")
    val maxDesire = max(activities.maxOfOrNull { it.genuineDesire } ?: 0, 100)
    val points = activities.sortedBy { it.date }
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .padding(vertical = 12.dp)
    ) {
        val stepX = size.width / (points.size - 1).coerceAtLeast(1)
        val stepY = size.height / maxDesire
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            drawLine(
                color = Color(0xFF8E24AA),
                start = androidx.compose.ui.geometry.Offset((i - 1) * stepX, size.height - prev.genuineDesire * stepY),
                end = androidx.compose.ui.geometry.Offset(i * stepX, size.height - curr.genuineDesire * stepY),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }
        points.forEachIndexed { index, entry ->
            drawCircle(
                color = Color.White,
                radius = 12f,
                center = androidx.compose.ui.geometry.Offset(index * stepX, size.height - entry.genuineDesire * stepY)
            )
            drawCircle(
                color = Color(0xFFAB47BC),
                radius = 8f,
                center = androidx.compose.ui.geometry.Offset(index * stepX, size.height - entry.genuineDesire * stepY)
            )
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        points.forEach {
            Text(it.date.format(formatter), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun EmotionSummary(entries: List<RelationshipEntry>) {
    val emotionCounts = entries.flatMap { it.emotions }.groupingBy { it }.eachCount()
    if (emotionCounts.isEmpty()) {
        Text("Registra emociones para ver el resumen", modifier = Modifier.padding(16.dp))
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Emociones más presentes", style = MaterialTheme.typography.titleMedium)
        emotionCounts.entries
            .sortedByDescending { it.value }
            .forEach { (id, count) ->
                val emotion = availableEmotions.firstOrNull { it.id == id }
                if (emotion != null) {
                    EmotionStatRow(emotion, count)
                }
            }
    }
}

@Composable
private fun EmotionStatRow(emotion: Emotion, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(emotion.colorHex.toInt()))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(emotion.icon)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(emotion.label, style = MaterialTheme.typography.titleMedium)
            Text("Aparece $count veces", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun HistoryView(
    entries: List<RelationshipEntry>,
    onEdit: (RelationshipEntry) -> Unit,
    onDelete: (RelationshipEntry) -> Unit
) {
    if (entries.isEmpty()) {
        EmptyState(icon = Icons.Default.History, message = "Tu historia aparecerá aquí")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(entries.sortedByDescending { it.createdAt }) { entry ->
            HistoryCard(entry = entry, onEdit = onEdit, onDelete = onDelete)
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun HistoryCard(entry: RelationshipEntry, onEdit: (RelationshipEntry) -> Unit, onDelete: (RelationshipEntry) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = if (entry.type == EntryType.ACTIVITY) entry.activity.ifBlank { "Actividad compartida" } else eventCategories.firstOrNull { it.id == entry.eventType }?.label ?: "Evento",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6A1B9A)
                    )
                    Text(entry.formattedTimestamp, style = MaterialTheme.typography.bodySmall)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { onEdit(entry) }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                    IconButton(onClick = { onDelete(entry) }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (entry.type == EntryType.ACTIVITY) {
                LabeledChip(title = "Deseo", value = "${entry.genuineDesire}%", color = Color(0xFFAB47BC))
            } else {
                LabeledChip(title = "Intensidad", value = entry.intensity.toString(), color = Color(0xFFBA68C8))
            }
            if (entry.emotions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowingEmotionRow(entry.emotions)
            }
            if (entry.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(entry.notes, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun LabeledChip(title: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodySmall, color = color)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

@Composable
private fun FlowingEmotionRow(emotions: List<String>) {
    val items = emotions.mapNotNull { id -> availableEmotions.firstOrNull { it.id == id } }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { emotion ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(emotion.colorHex.toInt()).copy(alpha = 0.18f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(emotion.icon)
                Spacer(modifier = Modifier.width(8.dp))
                Text(emotion.label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF7B1FA2), modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onImport: (List<RelationshipEntry>) -> Unit
) {
    var isFirestoreFormat by remember { mutableStateOf(true) }
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Importar datos", style = MaterialTheme.typography.titleLarge)
            Text("Para mantener la serenidad de tus registros puedes importar datos desde archivos JSON previamente exportados o desde Firestore.")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = isFirestoreFormat, onCheckedChange = { isFirestoreFormat = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isFirestoreFormat) "Importar desde Firestore" else "Importar desde archivo")
            }
            Button(onClick = {
                // TODO: add Firebase import when configured.
                onDismiss()
            }) {
                Text("Conectar con Firebase próximamente")
            }
        }
    }
}

private fun calculateTrend(activities: List<RelationshipEntry>): Int {
    if (activities.size < 4) return 0
    val sorted = activities.sortedBy { it.date }
    val recent = sorted.takeLast(3).map { it.genuineDesire }.average()
    val previous = sorted.dropLast(3).takeLast(3).map { it.genuineDesire }.average()
    return (recent - previous).toInt()
}

private fun exportEntries(cacheDir: File, entries: List<RelationshipEntry>) {
    // Placeholder for export logic. On a real device, use Storage Access Framework.
    // This function can be wired to share JSON data with other apps.
}

@Composable
private fun SerenityTextField(
    title: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    maxLines: Int = 2
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.material3.OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },
                maxLines = maxLines,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun SerenitySlider(value: Int, onValueChange: (Int) -> Unit) {
    androidx.compose.material3.Slider(
        value = value.toFloat(),
        onValueChange = { onValueChange(it.toInt()) },
        valueRange = 0f..100f
    )
}

@Composable
private fun SerenityCalendar(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    val start = today.minusMonths(3)
    val days = generateSequence(start) { it.plusDays(1) }
        .take(180)
        .toList()
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(top = 12.dp)
    ) {
        items(days) { day ->
            val isSelected = day == selectedDate
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFFBA68C8) else Color.White)
                    .clickable { onDateSelected(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.dayOfMonth.toString(),
                    color = if (isSelected) Color.White else Color(0xFF6A1B9A)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSerene() {
    SereneApp(viewModel = DiaryViewModel())
}
