package com.example.data.model

import java.util.Calendar
import java.util.Locale

data class CalendarDay(
  val year: Int,
  val month: Int, // 1 - 12
  val day: Int,
  val dayOfWeek: Int, // Calendar.MONDAY = 2 ... Calendar.SUNDAY = 1
  val isCurrentMonth: Boolean,
  val isToday: Boolean,
  val isSelected: Boolean,
  val isHoliday: Boolean = false,
  val holidayName: String? = null,
  val eventCount: Int = 0,
  val eventColors: List<Long> = emptyList()
) {
  val dateString: String
    get() = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month, day)

  val isSunday: Boolean
    get() = dayOfWeek == Calendar.SUNDAY
}

object CalendarHelper {

  private val MONTH_NAMES_ID = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
  )

  private val DAY_NAMES_ID = mapOf(
    Calendar.SUNDAY to "Minggu",
    Calendar.MONDAY to "Senin",
    Calendar.TUESDAY to "Selasa",
    Calendar.WEDNESDAY to "Rabu",
    Calendar.THURSDAY to "Kamis",
    Calendar.FRIDAY to "Jumat",
    Calendar.SATURDAY to "Sabtu"
  )

  fun getMonthName(month: Int): String {
    if (month in 1..12) return MONTH_NAMES_ID[month - 1]
    return ""
  }

  fun getDayName(dayOfWeek: Int): String {
    return DAY_NAMES_ID[dayOfWeek] ?: ""
  }

  fun formatIsoDate(year: Int, month: Int, day: Int): String {
    return String.format(Locale.ROOT, "%04d-%02d-%02d", year, month, day)
  }

  fun formatIndonesianDate(year: Int, month: Int, day: Int): String {
    val cal = Calendar.getInstance().apply {
      set(Calendar.YEAR, year)
      set(Calendar.MONTH, month - 1)
      set(Calendar.DAY_OF_MONTH, day)
    }
    val dayName = getDayName(cal.get(Calendar.DAY_OF_WEEK))
    val monthName = getMonthName(month)
    return "$dayName, $day $monthName $year"
  }

  fun getDaysForMonthGrid(
    year: Int,
    month: Int,
    selectedDateStr: String,
    todayDateStr: String,
    eventsMap: Map<String, List<CalendarEvent>>
  ): List<CalendarDay> {
    val daysList = mutableListOf<CalendarDay>()

    // Calendar for the 1st of current month
    val cal = Calendar.getInstance().apply {
      set(Calendar.YEAR, year)
      set(Calendar.MONTH, month - 1)
      set(Calendar.DAY_OF_MONTH, 1)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }

    val daysInCurrentMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday, ..., 7=Saturday

    // We use Monday as start of week:
    // Monday (2) -> 0 leading days
    // Tuesday (3) -> 1 leading day
    // Wednesday (4) -> 2
    // Thursday (5) -> 3
    // Friday (6) -> 4
    // Saturday (7) -> 5
    // Sunday (1) -> 6 leading days
    val leadingDays = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - Calendar.MONDAY

    // Previous month info
    val prevCal = (cal.clone() as Calendar).apply {
      add(Calendar.MONTH, -1)
    }
    val prevMonthYear = prevCal.get(Calendar.YEAR)
    val prevMonth = prevCal.get(Calendar.MONTH) + 1
    val prevMonthMaxDays = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Add leading days
    for (i in leadingDays - 1 downTo 0) {
      val dayNum = prevMonthMaxDays - i
      val dateStr = formatIsoDate(prevMonthYear, prevMonth, dayNum)
      val dayCal = Calendar.getInstance().apply {
        set(Calendar.YEAR, prevMonthYear)
        set(Calendar.MONTH, prevMonth - 1)
        set(Calendar.DAY_OF_MONTH, dayNum)
      }
      val holiday = IndonesianHolidays.getHolidayForDate(dateStr)
      val events = eventsMap[dateStr] ?: emptyList()

      daysList.add(
        CalendarDay(
          year = prevMonthYear,
          month = prevMonth,
          day = dayNum,
          dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK),
          isCurrentMonth = false,
          isToday = dateStr == todayDateStr,
          isSelected = dateStr == selectedDateStr,
          isHoliday = holiday?.isNationalHoliday == true,
          holidayName = holiday?.name,
          eventCount = events.size,
          eventColors = events.map { it.colorHex }.distinct()
        )
      )
    }

    // Add days of current month
    for (dayNum in 1..daysInCurrentMonth) {
      val dateStr = formatIsoDate(year, month, dayNum)
      val dayCal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, dayNum)
      }
      val holiday = IndonesianHolidays.getHolidayForDate(dateStr)
      val events = eventsMap[dateStr] ?: emptyList()

      daysList.add(
        CalendarDay(
          year = year,
          month = month,
          day = dayNum,
          dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK),
          isCurrentMonth = true,
          isToday = dateStr == todayDateStr,
          isSelected = dateStr == selectedDateStr,
          isHoliday = holiday?.isNationalHoliday == true,
          holidayName = holiday?.name,
          eventCount = events.size,
          eventColors = events.map { it.colorHex }.distinct()
        )
      )
    }

    // Trailing days from next month to make grid 35 or 42 (5 or 6 weeks)
    val totalCells = if (daysList.size <= 35) 35 else 42
    val trailingDaysCount = totalCells - daysList.size

    val nextCal = (cal.clone() as Calendar).apply {
      add(Calendar.MONTH, 1)
    }
    val nextMonthYear = nextCal.get(Calendar.YEAR)
    val nextMonth = nextCal.get(Calendar.MONTH) + 1

    for (dayNum in 1..trailingDaysCount) {
      val dateStr = formatIsoDate(nextMonthYear, nextMonth, dayNum)
      val dayCal = Calendar.getInstance().apply {
        set(Calendar.YEAR, nextMonthYear)
        set(Calendar.MONTH, nextMonth - 1)
        set(Calendar.DAY_OF_MONTH, dayNum)
      }
      val holiday = IndonesianHolidays.getHolidayForDate(dateStr)
      val events = eventsMap[dateStr] ?: emptyList()

      daysList.add(
        CalendarDay(
          year = nextMonthYear,
          month = nextMonth,
          day = dayNum,
          dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK),
          isCurrentMonth = false,
          isToday = dateStr == todayDateStr,
          isSelected = dateStr == selectedDateStr,
          isHoliday = holiday?.isNationalHoliday == true,
          holidayName = holiday?.name,
          eventCount = events.size,
          eventColors = events.map { it.colorHex }.distinct()
        )
      )
    }

    return daysList
  }
}
