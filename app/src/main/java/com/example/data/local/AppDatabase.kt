package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CalendarEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(entities = [CalendarEvent::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

  abstract fun eventDao(): EventDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "calendar_database"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              // Seed sample events on first creation
              INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                  val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
                  val sampleEvents = listOf(
                    CalendarEvent(
                      title = "Selamat Datang di Kalender!",
                      description = "Kelola agenda, rencana harian, dan catatan penting Anda dengan mudah di sini.",
                      date = todayStr,
                      startTime = "09:00",
                      endTime = "10:00",
                      isAllDay = false,
                      category = CalendarEvent.CATEGORY_PERSONAL,
                      colorHex = 0xFF2563EB
                    ),
                    CalendarEvent(
                      title = "Rapat Tim & Diskusi Proyek",
                      description = "Evaluasi target bulanan dan perencanaan jadwal minggu ini.",
                      date = todayStr,
                      startTime = "14:00",
                      endTime = "15:30",
                      isAllDay = false,
                      category = CalendarEvent.CATEGORY_WORK,
                      colorHex = 0xFF0D9488
                    )
                  )
                  database.eventDao().insertEvents(sampleEvents)
                }
              }
            }
          })
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
