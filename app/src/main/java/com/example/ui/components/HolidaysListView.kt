package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.CalendarHelper
import com.example.data.model.IndonesianHoliday
import com.example.ui.theme.AccentRed

@Composable
fun HolidaysListView(
  holidays: List<IndonesianHoliday>,
  selectedYear: Int,
  onHolidayClick: (IndonesianHoliday) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("holidays_list_view")
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // Info Banner
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
      ),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Celebration,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "Daftar Hari Libur Nasional $selectedYear",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = "Ketuk hari libur untuk melihatnya langsung di kalender.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(holidays, key = { it.date + it.name }) { holiday ->
        HolidayCard(
          holiday = holiday,
          onClick = { onHolidayClick(holiday) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}

@Composable
fun HolidayCard(
  holiday: IndonesianHoliday,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Parse date: YYYY-MM-DD
  val parts = holiday.date.split("-")
  val dayNum = parts.getOrNull(2)?.toIntOrNull() ?: 1
  val monthNum = parts.getOrNull(1)?.toIntOrNull() ?: 1
  val yearNum = parts.getOrNull(0)?.toIntOrNull() ?: 2026
  val formattedDate = CalendarHelper.formatIndonesianDate(yearNum, monthNum, dayNum)

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("holiday_card_${holiday.date}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Date badge box
      Box(
        modifier = Modifier
          .size(50.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(if (holiday.isNationalHoliday) AccentRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = dayNum.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (holiday.isNationalHoliday) AccentRed else MaterialTheme.colorScheme.primary,
            lineHeight = 18.sp
          )
          Text(
            text = CalendarHelper.getMonthName(monthNum).take(3),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (holiday.isNationalHoliday) AccentRed else MaterialTheme.colorScheme.primary,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = holiday.name,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = formattedDate,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (holiday.isNationalHoliday) {
          Spacer(modifier = Modifier.height(4.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(AccentRed.copy(alpha = 0.1f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "Tanggal Merah / Libur Nasional",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Medium,
              color = AccentRed,
              fontSize = 10.sp
            )
          }
        }
      }

      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = "Buka di Kalender",
        tint = MaterialTheme.colorScheme.outline
      )
    }
  }
}
