package com.example

import com.example.data.model.CalendarHelper
import com.example.data.model.IndonesianHolidays
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarHelperTest {

  @Test
  fun testIndonesianMonthNames() {
    assertEquals("Januari", CalendarHelper.getMonthName(1))
    assertEquals("Oktober", CalendarHelper.getMonthName(10))
    assertEquals("Desember", CalendarHelper.getMonthName(12))
  }

  @Test
  fun testIndonesianHolidays() {
    val kemerdekaan = IndonesianHolidays.getHolidayForDate("2026-08-17")
    assertNotNull(kemerdekaan)
    assertTrue(kemerdekaan!!.name.contains("Kemerdekaan"))
    assertTrue(kemerdekaan.isNationalHoliday)

    val holidays2026 = IndonesianHolidays.getHolidaysForYear(2026)
    assertTrue(holidays2026.isNotEmpty())
  }

  @Test
  fun testMonthGridGeneration() {
    val grid = CalendarHelper.getDaysForMonthGrid(
      year = 2026,
      month = 10,
      selectedDateStr = "2026-10-07",
      todayDateStr = "2026-10-07",
      eventsMap = emptyMap()
    )

    // Grid must be 35 or 42 cells
    assertTrue(grid.size == 35 || grid.size == 42)
    val todayCell = grid.find { it.isToday }
    assertNotNull(todayCell)
    assertEquals(7, todayCell!!.day)
    assertEquals(10, todayCell.month)
    assertEquals(2026, todayCell.year)
  }
}
