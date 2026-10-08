package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

  @Query("SELECT * FROM calendar_events ORDER BY date ASC, startTime ASC")
  fun getAllEvents(): Flow<List<CalendarEvent>>

  @Query("SELECT * FROM calendar_events WHERE date = :dateStr ORDER BY isCompleted ASC, startTime ASC")
  fun getEventsForDate(dateStr: String): Flow<List<CalendarEvent>>

  @Query("SELECT * FROM calendar_events WHERE date LIKE :monthPrefix || '%' ORDER BY date ASC, startTime ASC")
  fun getEventsForMonth(monthPrefix: String): Flow<List<CalendarEvent>>

  @Query("SELECT * FROM calendar_events WHERE date >= :currentDate ORDER BY date ASC, startTime ASC")
  fun getUpcomingEvents(currentDate: String): Flow<List<CalendarEvent>>

  @Query("SELECT * FROM calendar_events WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY date ASC")
  fun searchEvents(query: String): Flow<List<CalendarEvent>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: CalendarEvent): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvents(events: List<CalendarEvent>)

  @Update
  suspend fun updateEvent(event: CalendarEvent)

  @Delete
  suspend fun deleteEvent(event: CalendarEvent)

  @Query("DELETE FROM calendar_events WHERE id = :id")
  suspend fun deleteEventById(id: Long)
}
