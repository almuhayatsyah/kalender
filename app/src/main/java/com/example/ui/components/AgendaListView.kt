package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalendarEvent
import com.example.data.model.CalendarHelper

@Composable
fun AgendaListView(
  events: List<CalendarEvent>,
  selectedCategory: String?,
  onSelectCategory: (String?) -> Unit,
  onToggleComplete: (CalendarEvent) -> Unit,
  onEditEvent: (CalendarEvent) -> Unit,
  onDeleteEvent: (CalendarEvent) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("agenda_list_view")
  ) {
    // Category Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // "Semua" Chip
      val isAll = selectedCategory == null
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(if (isAll) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
          .clickable { onSelectCategory(null) }
          .padding(horizontal = 14.dp, vertical = 8.dp)
          .testTag("filter_all")
      ) {
        Text(
          text = "Semua",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
          color = if (isAll) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Categories chips
      CalendarEvent.CATEGORIES.forEach { (catName, catColor) ->
        val isSelected = selectedCategory == catName
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(catColor) else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onSelectCategory(if (isSelected) null else catName) }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("filter_$catName")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.White else Color(catColor))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = catName,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    if (events.isEmpty()) {
      // Empty State
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.EventAvailable,
          contentDescription = null,
          modifier = Modifier.size(64.dp),
          tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Belum Ada Jadwal",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Ketuk tombol tambah (+) untuk menjadwalkan agenda atau catatan baru.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      }
    } else {
      // Group events by date
      val groupedEvents = events.groupBy { it.date }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        groupedEvents.forEach { (dateStr, dateEvents) ->
          item(key = "header_$dateStr") {
            val parts = dateStr.split("-")
            val y = parts.getOrNull(0)?.toIntOrNull() ?: 2026
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 1
            val d = parts.getOrNull(2)?.toIntOrNull() ?: 1
            val headerFormatted = CalendarHelper.formatIndonesianDate(y, m, d)

            Text(
              text = headerFormatted,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
            )
          }

          items(dateEvents, key = { it.id }) { event ->
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
  }
}
