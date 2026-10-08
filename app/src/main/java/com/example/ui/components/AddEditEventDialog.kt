package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CalendarEvent

@Composable
fun AddEditEventDialog(
  initialEvent: CalendarEvent?,
  defaultDate: String,
  onDismiss: () -> Unit,
  onSave: (
    id: Long,
    title: String,
    description: String,
    date: String,
    startTime: String,
    endTime: String,
    isAllDay: Boolean,
    category: String,
    colorHex: Long,
    location: String
  ) -> Unit
) {
  var title by remember { mutableStateOf(initialEvent?.title ?: "") }
  var description by remember { mutableStateOf(initialEvent?.description ?: "") }
  var dateStr by remember { mutableStateOf(initialEvent?.date ?: defaultDate) }
  var isAllDay by remember { mutableStateOf(initialEvent?.isAllDay ?: false) }
  var startTime by remember { mutableStateOf(initialEvent?.startTime ?: "09:00") }
  var endTime by remember { mutableStateOf(initialEvent?.endTime ?: "10:00") }
  var selectedCategory by remember { mutableStateOf(initialEvent?.category ?: CalendarEvent.CATEGORY_PERSONAL) }
  var selectedColorHex by remember { mutableLongStateOf(initialEvent?.colorHex ?: 0xFF2563EB) }
  var location by remember { mutableStateOf(initialEvent?.location ?: "") }
  var titleError by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("add_edit_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (initialEvent == null) "Tambah Agenda Baru" else "Edit Agenda",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title Field
        OutlinedTextField(
          value = title,
          onValueChange = {
            title = it
            if (it.isNotBlank()) titleError = false
          },
          label = { Text("Judul Acara / Catatan") },
          leadingIcon = {
            Icon(Icons.Default.Title, contentDescription = null)
          },
          isError = titleError,
          supportingText = {
            if (titleError) Text("Judul tidak boleh kosong", color = MaterialTheme.colorScheme.error)
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("event_title_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Selection
        Text(
          text = "Kategori",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CalendarEvent.CATEGORIES.forEach { (catName, catColor) ->
            val isSelected = selectedCategory == catName
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) Color(catColor) else MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                  selectedCategory = catName
                  selectedColorHex = catColor
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
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
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date String Field
        OutlinedTextField(
          value = dateStr,
          onValueChange = { dateStr = it },
          label = { Text("Tanggal (YYYY-MM-DD)") },
          leadingIcon = {
            Icon(Icons.Default.CalendarMonth, contentDescription = null)
          },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // All-day Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Acara Sepanjang Hari",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
          )
          Switch(
            checked = isAllDay,
            onCheckedChange = { isAllDay = it },
            modifier = Modifier.testTag("all_day_switch")
          )
        }

        // Time pickers if not all-day
        if (!isAllDay) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            OutlinedTextField(
              value = startTime,
              onValueChange = { startTime = it },
              label = { Text("Mulai (JJ:MM)") },
              leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
            OutlinedTextField(
              value = endTime,
              onValueChange = { endTime = it },
              label = { Text("Selesai (JJ:MM)") },
              leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Location Field
        OutlinedTextField(
          value = location,
          onValueChange = { location = it },
          label = { Text("Lokasi (Opsional)") },
          leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description / Notes Field
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Catatan / Deskripsi Tambahan") },
          leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Batal")
          }
          Button(
            onClick = {
              if (title.isBlank()) {
                titleError = true
              } else {
                onSave(
                  initialEvent?.id ?: 0L,
                  title.trim(),
                  description.trim(),
                  dateStr.trim(),
                  startTime.trim(),
                  endTime.trim(),
                  isAllDay,
                  selectedCategory,
                  selectedColorHex,
                  location.trim()
                )
              }
            },
            modifier = Modifier
              .weight(1f)
              .testTag("save_event_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Simpan")
          }
        }
      }
    }
  }
}
