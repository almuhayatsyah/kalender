package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalendarDay
import com.example.ui.theme.AccentRed

@Composable
fun MonthGrid(
  days: List<CalendarDay>,
  onDayClick: (CalendarDay) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp)
      .testTag("month_grid")
  ) {
    // Days of week header: Sen, Sel, Rab, Kam, Jum, Sab, Min
    val dayHeaders = listOf(
      "Sen" to false,
      "Sel" to false,
      "Rab" to false,
      "Kam" to false,
      "Jum" to false,
      "Sab" to false,
      "Min" to true // Minggu is red
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      dayHeaders.forEach { (name, isRed) ->
        Text(
          text = name,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = if (isRed) AccentRed else MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Grid of days (chunks of 7)
    days.chunked(7).forEach { week ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        week.forEach { day ->
          DayCell(
            day = day,
            onClick = { onDayClick(day) },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}

@Composable
fun DayCell(
  day: CalendarDay,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isRedDay = day.isSunday || day.isHoliday
  val cellBackgroundColor by animateColorAsState(
    targetValue = when {
      day.isSelected -> MaterialTheme.colorScheme.primary
      day.isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
      else -> Color.Transparent
    },
    label = "cell_bg"
  )

  val textColor = when {
    day.isSelected -> MaterialTheme.colorScheme.onPrimary
    !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
    isRedDay -> AccentRed
    day.isToday -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.onSurface
  }

  Box(
    modifier = modifier
      .aspectRatio(1f)
      .padding(2.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(cellBackgroundColor)
      .then(
        if (day.isToday && !day.isSelected) {
          Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
        } else {
          Modifier
        }
      )
      .clickable { onClick() }
      .testTag("day_cell_${day.dateString}"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(2.dp)
    ) {
      Text(
        text = day.day.toString(),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (day.isSelected || day.isToday) FontWeight.Bold else FontWeight.Medium,
        color = textColor,
        fontSize = 14.sp
      )

      Spacer(modifier = Modifier.height(2.dp))

      // Event dots indicator
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(6.dp)
      ) {
        if (day.isHoliday && day.holidayName != null && !day.isSelected) {
          Box(
            modifier = Modifier
              .size(4.dp)
              .clip(CircleShape)
              .background(AccentRed)
          )
          if (day.eventCount > 0) {
            Spacer(modifier = Modifier.width(2.dp))
          }
        }

        if (day.eventColors.isNotEmpty()) {
          val dots = day.eventColors.take(3)
          dots.forEachIndexed { index, colorHex ->
            val dotColor = if (day.isSelected) {
              MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            } else {
              Color(colorHex)
            }
            Box(
              modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(dotColor)
            )
            if (index < dots.size - 1) {
              Spacer(modifier = Modifier.width(2.dp))
            }
          }
        } else if (day.eventCount > 0) {
          Box(
            modifier = Modifier
              .size(4.dp)
              .clip(CircleShape)
              .background(if (day.isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
          )
        }
      }
    }
  }
}
