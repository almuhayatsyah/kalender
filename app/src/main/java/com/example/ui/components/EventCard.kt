package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalendarEvent

@Composable
fun EventCard(
  event: CalendarEvent,
  onToggleComplete: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val eventColor = Color(event.colorHex)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp, horizontal = 12.dp)
      .testTag("event_card_${event.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (event.isCompleted) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (event.isCompleted) 0.dp else 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.Top
    ) {
      // Color accent indicator bar
      Box(
        modifier = Modifier
          .width(4.dp)
          .height(54.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(if (event.isCompleted) Color.Gray else eventColor)
      )

      Spacer(modifier = Modifier.width(10.dp))

      // Checkbox / completion toggle
      IconButton(
        onClick = onToggleComplete,
        modifier = Modifier
          .size(36.dp)
          .testTag("toggle_complete_${event.id}")
      ) {
        Icon(
          imageVector = if (event.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
          contentDescription = if (event.isCompleted) "Selesai" else "Tandai selesai",
          tint = if (event.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Event content details
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = event.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (event.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            color = if (event.isCompleted) {
              MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            } else {
              MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.weight(1f)
          )

          // Category tag
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(eventColor.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = event.category,
              color = eventColor,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Time row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(vertical = 2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = "Waktu",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (event.isAllDay) {
              "Sepanjang Hari"
            } else if (event.startTime.isNotBlank() && event.endTime.isNotBlank()) {
              "${event.startTime} - ${event.endTime}"
            } else if (event.startTime.isNotBlank()) {
              event.startTime
            } else {
              "Waktu fleksibel"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          if (event.location.isNotBlank()) {
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = "Lokasi",
              modifier = Modifier.size(14.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = event.location,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1
            )
          }
        }

        if (event.description.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = event.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            fontSize = 13.sp
          )
        }
      }

      // Actions: Edit and Delete
      Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
      ) {
        IconButton(
          onClick = onEdit,
          modifier = Modifier
            .size(32.dp)
            .testTag("edit_event_${event.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Acara",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(32.dp)
            .testTag("delete_event_${event.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Hapus Acara",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.error
          )
        }
      }
    }
  }
}
