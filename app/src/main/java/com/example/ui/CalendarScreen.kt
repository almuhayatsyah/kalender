package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.CalendarEvent
import com.example.data.model.CalendarHelper
import com.example.data.model.IndonesianHoliday
import com.example.ui.components.AddEditEventDialog
import com.example.ui.components.AgendaListView
import com.example.ui.components.CalendarTopBar
import com.example.ui.components.EventCard
import com.example.ui.components.HolidaysListView
import com.example.ui.components.MonthGrid
import com.example.ui.theme.AccentRed

@Composable
fun CalendarScreen(
  viewModel: CalendarViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val displayedYear by viewModel.displayedYear.collectAsStateWithLifecycle()
  val displayedMonth by viewModel.displayedMonth.collectAsStateWithLifecycle()
  val selectedDateStr by viewModel.selectedDate.collectAsStateWithLifecycle()
  val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
  val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

  val calendarGrid by viewModel.calendarGrid.collectAsStateWithLifecycle()
  val eventsForSelectedDate by viewModel.eventsForSelectedDate.collectAsStateWithLifecycle()
  val upcomingEvents by viewModel.upcomingEvents.collectAsStateWithLifecycle()
  val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
  val selectedDateHoliday by viewModel.selectedDateHoliday.collectAsStateWithLifecycle()
  val currentYearHolidays by viewModel.currentYearHolidays.collectAsStateWithLifecycle()

  // Add/Edit Event dialog state
  var showAddEditDialog by remember { mutableStateOf(false) }
  var eventToEdit by remember { mutableStateOf<CalendarEvent?>(null) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      CalendarTopBar(
        year = displayedYear,
        month = displayedMonth,
        onPreviousMonth = { viewModel.previousMonth() },
        onNextMonth = { viewModel.nextMonth() },
        onJumpToToday = { viewModel.jumpToToday() },
        onSelectYearMonth = { y, m -> viewModel.setYearMonth(y, m) },
        currentViewMode = viewMode,
        onViewModeChanged = { viewModel.setViewMode(it) },
        isSearchActive = isSearchActive,
        searchQuery = searchQuery,
        onToggleSearch = { viewModel.toggleSearch(it) },
        onSearchQueryChanged = { viewModel.setSearchQuery(it) }
      )
    },
    floatingActionButton = {
      if (viewMode != CalendarViewMode.HOLIDAYS && !isSearchActive) {
        ExtendedFloatingActionButton(
          onClick = {
            eventToEdit = null
            showAddEditDialog = true
          },
          icon = { Icon(Icons.Default.Add, contentDescription = null) },
          text = { Text("Tambah Acara") },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.testTag("add_event_fab")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (isSearchActive) {
        // Search Results List
        SearchResultsView(
          query = searchQuery,
          results = searchResults,
          onToggleComplete = { viewModel.toggleEventCompletion(it) },
          onEditEvent = {
            eventToEdit = it
            showAddEditDialog = true
          },
          onDeleteEvent = { viewModel.deleteEvent(it) }
        )
      } else {
        when (viewMode) {
          CalendarViewMode.MONTH -> {
            MonthAndDayView(
              calendarGrid = calendarGrid,
              selectedDateStr = selectedDateStr,
              selectedDateHoliday = selectedDateHoliday,
              events = eventsForSelectedDate,
              onDayClick = { viewModel.onDaySelected(it) },
              onToggleComplete = { viewModel.toggleEventCompletion(it) },
              onEditEvent = {
                eventToEdit = it
                showAddEditDialog = true
              },
              onDeleteEvent = { viewModel.deleteEvent(it) },
              onAddEventClick = {
                eventToEdit = null
                showAddEditDialog = true
              }
            )
          }

          CalendarViewMode.AGENDA -> {
            AgendaListView(
              events = upcomingEvents,
              selectedCategory = selectedCategoryFilter,
              onSelectCategory = { viewModel.setCategoryFilter(it) },
              onToggleComplete = { viewModel.toggleEventCompletion(it) },
              onEditEvent = {
                eventToEdit = it
                showAddEditDialog = true
              },
              onDeleteEvent = { viewModel.deleteEvent(it) }
            )
          }

          CalendarViewMode.HOLIDAYS -> {
            HolidaysListView(
              holidays = currentYearHolidays,
              selectedYear = displayedYear,
              onHolidayClick = { holiday ->
                viewModel.setSelectedDateString(holiday.date)
                viewModel.setViewMode(CalendarViewMode.MONTH)
              }
            )
          }
        }
      }
    }
  }

  // Add / Edit Dialog
  if (showAddEditDialog) {
    AddEditEventDialog(
      initialEvent = eventToEdit,
      defaultDate = selectedDateStr,
      onDismiss = {
        showAddEditDialog = false
        eventToEdit = null
      },
      onSave = { id, title, desc, date, start, end, allDay, cat, color, loc ->
        viewModel.saveEvent(
          id = id,
          title = title,
          description = desc,
          date = date,
          startTime = start,
          endTime = end,
          isAllDay = allDay,
          category = cat,
          colorHex = color,
          location = loc
        )
        showAddEditDialog = false
        eventToEdit = null
      }
    )
  }
}

@Composable
fun MonthAndDayView(
  calendarGrid: List<com.example.data.model.CalendarDay>,
  selectedDateStr: String,
  selectedDateHoliday: IndonesianHoliday?,
  events: List<CalendarEvent>,
  onDayClick: (com.example.data.model.CalendarDay) -> Unit,
  onToggleComplete: (CalendarEvent) -> Unit,
  onEditEvent: (CalendarEvent) -> Unit,
  onDeleteEvent: (CalendarEvent) -> Unit,
  onAddEventClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val dateParts = selectedDateStr.split("-")
  val y = dateParts.getOrNull(0)?.toIntOrNull() ?: 2026
  val m = dateParts.getOrNull(1)?.toIntOrNull() ?: 1
  val d = dateParts.getOrNull(2)?.toIntOrNull() ?: 1
  val formattedSelectedDate = CalendarHelper.formatIndonesianDate(y, m, d)

  LazyColumn(
    modifier = modifier.fillMaxSize()
  ) {
    // 1. Calendar Grid
    item {
      MonthGrid(
        days = calendarGrid,
        onDayClick = onDayClick
      )
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
      Spacer(modifier = Modifier.height(10.dp))
    }

    // 2. Selected Date Header Banner
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.DateRange,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = formattedSelectedDate,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Text(
            text = "${events.size} Agenda",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Holiday banner if today is an official Indonesian holiday
        if (selectedDateHoliday != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (selectedDateHoliday.isNationalHoliday) AccentRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = null,
                tint = if (selectedDateHoliday.isNationalHoliday) AccentRed else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = selectedDateHoliday.name,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = if (selectedDateHoliday.isNationalHoliday) AccentRed else MaterialTheme.colorScheme.primary
                )
                if (selectedDateHoliday.isNationalHoliday) {
                  Text(
                    text = "Hari Libur Nasional Indonesia (Tanggal Merah)",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentRed.copy(alpha = 0.9f)
                  )
                }
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    // 3. Events for Selected Date
    if (events.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.EventNote,
              contentDescription = null,
              modifier = Modifier.size(40.dp),
              tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Tidak ada agenda untuk tanggal ini",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Ketuk tombol 'Tambah Acara' untuk membuat rencana baru.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }
      }
    } else {
      items(events, key = { it.id }) { event ->
        EventCard(
          event = event,
          onToggleComplete = { onToggleComplete(event) },
          onEdit = { onEditEvent(event) },
          onDelete = { onDeleteEvent(event) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(88.dp))
    }
  }
}

@Composable
fun SearchResultsView(
  query: String,
  results: List<CalendarEvent>,
  onToggleComplete: (CalendarEvent) -> Unit,
  onEditEvent: (CalendarEvent) -> Unit,
  onDeleteEvent: (CalendarEvent) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("search_results_view")
  ) {
    Text(
      text = if (query.isBlank()) "Ketik kata kunci untuk mencari" else "Hasil pencarian '${query}' (${results.size})",
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(vertical = 12.dp)
    )

    if (results.isEmpty() && query.isNotBlank()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Tidak ditemukan jadwal yang sesuai",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.outline
        )
      }
    } else {
      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(results, key = { it.id }) { event ->
          EventCard(
            event = event,
            onToggleComplete = { onToggleComplete(event) },
            onEdit = { onEditEvent(event) },
            onDelete = { onDeleteEvent(event) }
          )
        }
        item {
          Spacer(modifier = Modifier.height(40.dp))
        }
      }
    }
  }
}
