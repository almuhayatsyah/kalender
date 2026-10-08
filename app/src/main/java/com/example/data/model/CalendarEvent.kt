package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEvent(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val description: String = "",
  val date: String, // Format: YYYY-MM-DD
  val startTime: String = "", // Format: HH:mm e.g. "09:00"
  val endTime: String = "", // Format: HH:mm e.g. "10:30"
  val isAllDay: Boolean = true,
  val category: String = CATEGORY_PERSONAL,
  val colorHex: Long = 0xFF2563EB,
  val isCompleted: Boolean = false,
  val location: String = ""
) {
  companion object {
    const val CATEGORY_PERSONAL = "Pribadi"
    const val CATEGORY_WORK = "Pekerjaan"
    const val CATEGORY_STUDY = "Pendidikan"
    const val CATEGORY_IMPORTANT = "Penting"
    const val CATEGORY_REMINDER = "Pengingat"

    val CATEGORIES = listOf(
      CATEGORY_PERSONAL to 0xFF2563EB,  // Biru
      CATEGORY_WORK to 0xFF0D9488,      // Teal
      CATEGORY_IMPORTANT to 0xFFDC2626, // Merah
      CATEGORY_STUDY to 0xFF8B5CF6,     // Ungu
      CATEGORY_REMINDER to 0xFFF59E0B   // Kuning/Amber
    )
  }
}
