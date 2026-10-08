package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CalendarDay
import com.example.data.model.CalendarEvent
import com.example.data.model.CalendarHelper
import com.example.data.model.IndonesianHoliday
import com.example.data.model.IndonesianHolidays
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

enum class CalendarViewMode {
  MONTH,    // Tampilan Kalender Bulanan
  AGENDA,   // Tampilan Daftar Agenda
  HOLIDAYS  // Daftar Hari Libur Nasional
}

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

  private val eventDao = AppDatabase.getDatabase(application).eventDao()

  private val todayCal = Calendar.getInstance()
  val todayYear = todayCal.get(Calendar.YEAR)
  val todayMonth = todayCal.get(Calendar.MONTH) + 1
  val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)
  val todayIsoString = CalendarHelper.formatIsoDate(todayYear, todayMonth, todayDay)

  private val _displayedYear = MutableStateFlow(todayYear)
  val displayedYear: StateFlow<Int> = _displayedYear.asStateFlow()

  private val _displayedMonth = MutableStateFlow(todayMonth)
  val displayedMonth: StateFlow<Int> = _displayedMonth.asStateFlow()

  private val _selectedDate = MutableStateFlow(todayIsoString)
  val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

  private val _viewMode = MutableStateFlow(CalendarViewMode.MONTH)
  val viewMode: StateFlow<CalendarViewMode> = _viewMode.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _isSearchActive = MutableStateFlow(false)
  val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

  private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
  val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

  // All events stream
  val allEvents: StateFlow<List<CalendarEvent>> = eventDao.getAllEvents()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Events map by date string for easy lookup
  val eventsByDate: StateFlow<Map<String, List<CalendarEvent>>> = allEvents
    .combine(_selectedCategoryFilter) { events, filter ->
      val filtered = if (filter == null) events else events.filter { it.category == filter }
      filtered.groupBy { it.date }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

  // Events for selected date
  val eventsForSelectedDate: StateFlow<List<CalendarEvent>> = combine(
    allEvents,
    _selectedDate,
    _selectedCategoryFilter
  ) { events, date, filter ->
    events.filter { it.date == date && (filter == null || it.category == filter) }
      .sortedWith(compareBy<CalendarEvent> { it.isCompleted }.thenBy { it.startTime })
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Upcoming agenda stream
  val upcomingEvents: StateFlow<List<CalendarEvent>> = combine(
    allEvents,
    _selectedCategoryFilter
  ) { events, filter ->
    events.filter { it.date >= todayIsoString && (filter == null || it.category == filter) }
      .sortedBy { it.date }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Search results
  val searchResults: StateFlow<List<CalendarEvent>> = combine(
    allEvents,
    _searchQuery
  ) { events, query ->
    if (query.isBlank()) emptyList()
    else events.filter {
      it.title.contains(query, ignoreCase = true) ||
          it.description.contains(query, ignoreCase = true) ||
          it.category.contains(query, ignoreCase = true) ||
          it.location.contains(query, ignoreCase = true)
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Calendar days grid for current displayed month
  val calendarGrid: StateFlow<List<CalendarDay>> = combine(
    _displayedYear,
    _displayedMonth,
    _selectedDate,
    eventsByDate
  ) { year, month, selectedDateStr, eventsMap ->
    CalendarHelper.getDaysForMonthGrid(
      year = year,
      month = month,
      selectedDateStr = selectedDateStr,
      todayDateStr = todayIsoString,
      eventsMap = eventsMap
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Selected date holiday info
  val selectedDateHoliday: StateFlow<IndonesianHoliday?> = _selectedDate
    .combine(_displayedYear) { date, _ ->
      IndonesianHolidays.getHolidayForDate(date)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Holidays for current displayed year
  val currentYearHolidays: StateFlow<List<IndonesianHoliday>> = _displayedYear
    .combine(_selectedDate) { year, _ ->
      IndonesianHolidays.getHolidaysForYear(year)
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IndonesianHolidays.getHolidaysForYear(todayYear))

  fun setViewMode(mode: CalendarViewMode) {
    _viewMode.value = mode
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun toggleSearch(active: Boolean) {
    _isSearchActive.value = active
    if (!active) {
      _searchQuery.value = ""
    }
  }

  fun setCategoryFilter(category: String?) {
    _selectedCategoryFilter.value = category
  }

  fun onDaySelected(day: CalendarDay) {
    _selectedDate.value = day.dateString
    if (day.month != _displayedMonth.value || day.year != _displayedYear.value) {
      _displayedYear.value = day.year
      _displayedMonth.value = day.month
    }
  }

  fun setSelectedDateString(dateStr: String) {
    _selectedDate.value = dateStr
    val parts = dateStr.split("-")
    if (parts.size == 3) {
      val y = parts[0].toIntOrNull() ?: todayYear
      val m = parts[1].toIntOrNull() ?: todayMonth
      _displayedYear.value = y
      _displayedMonth.value = m
    }
  }

  fun previousMonth() {
    if (_displayedMonth.value == 1) {
      _displayedMonth.value = 12
      _displayedYear.value -= 1
    } else {
      _displayedMonth.value -= 1
    }
  }

  fun nextMonth() {
    if (_displayedMonth.value == 12) {
      _displayedMonth.value = 1
      _displayedYear.value += 1
    } else {
      _displayedMonth.value += 1
    }
  }

  fun jumpToToday() {
    _displayedYear.value = todayYear
    _displayedMonth.value = todayMonth
    _selectedDate.value = todayIsoString
  }

  fun setYearMonth(year: Int, month: Int) {
    _displayedYear.value = year
    _displayedMonth.value = month
  }

  fun saveEvent(
    id: Long = 0,
    title: String,
    description: String,
    date: String,
    startTime: String,
    endTime: String,
    isAllDay: Boolean,
    category: String,
    colorHex: Long,
    location: String
  ) {
    viewModelScope.launch {
      val event = CalendarEvent(
        id = id,
        title = title,
        description = description,
        date = date,
        startTime = startTime,
        endTime = endTime,
        isAllDay = isAllDay,
        category = category,
        colorHex = colorHex,
        location = location
      )
      if (id == 0L) {
        eventDao.insertEvent(event)
      } else {
        eventDao.updateEvent(event)
      }
    }
  }

  fun toggleEventCompletion(event: CalendarEvent) {
    viewModelScope.launch {
      eventDao.updateEvent(event.copy(isCompleted = !event.isCompleted))
    }
  }

  fun deleteEvent(event: CalendarEvent) {
    viewModelScope.launch {
      eventDao.deleteEvent(event)
    }
  }
}
