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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalendarHelper
import com.example.ui.CalendarViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(
  year: Int,
  month: Int,
  onPreviousMonth: () -> Unit,
  onNextMonth: () -> Unit,
  onJumpToToday: () -> Unit,
  onSelectYearMonth: (year: Int, month: Int) -> Unit,
  currentViewMode: CalendarViewMode,
  onViewModeChanged: (CalendarViewMode) -> Unit,
  isSearchActive: Boolean,
  searchQuery: String,
  onToggleSearch: (Boolean) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var showMonthPicker by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface)
      .padding(top = 8.dp)
  ) {
    if (isSearchActive) {
      // Active Search Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChanged,
          placeholder = { Text("Cari jadwal, catatan, atau kategori...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
          },
          trailingIcon = {
            IconButton(onClick = { onToggleSearch(false) }) {
              Icon(Icons.Default.Close, contentDescription = "Tutup pencarian")
            }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_text_field"),
          shape = RoundedCornerShape(16.dp)
        )
      }
    } else {
      // Main App & Month Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Month & Year Picker clickable
        Box {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable { showMonthPicker = true }
              .padding(horizontal = 8.dp, vertical = 6.dp)
              .testTag("month_picker_trigger")
          ) {
            Icon(
              imageVector = Icons.Default.EventNote,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "${CalendarHelper.getMonthName(month)} $year",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Pilih Bulan dan Tahun",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Quick Month Dropdown
          DropdownMenu(
            expanded = showMonthPicker,
            onDismissRequest = { showMonthPicker = false }
          ) {
            for (m in 1..12) {
              DropdownMenuItem(
                text = {
                  Text(
                    text = "${CalendarHelper.getMonthName(m)} $year",
                    fontWeight = if (m == month) FontWeight.Bold else FontWeight.Normal,
                    color = if (m == month) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                  )
                },
                onClick = {
                  onSelectYearMonth(year, m)
                  showMonthPicker = false
                }
              )
            }
          }
        }

        // Actions: Hari Ini, Navigasi Bulan, Cari
        Row(verticalAlignment = Alignment.CenterVertically) {
          // "Hari Ini" quick button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(MaterialTheme.colorScheme.primaryContainer)
              .clickable { onJumpToToday() }
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("jump_today_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Hari Ini",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Prev Month
          IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier
              .size(36.dp)
              .testTag("prev_month_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Bulan Sebelumnya",
              modifier = Modifier.size(18.dp)
            )
          }

          // Next Month
          IconButton(
            onClick = onNextMonth,
            modifier = Modifier
              .size(36.dp)
              .testTag("next_month_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Bulan Berikutnya",
              modifier = Modifier.size(18.dp)
            )
          }

          // Search button
          IconButton(
            onClick = { onToggleSearch(true) },
            modifier = Modifier
              .size(36.dp)
              .testTag("search_button")
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Cari",
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    // Tabs / Modes: Bulan, Agenda, Hari Libur
    val tabs = listOf(
      CalendarViewMode.MONTH to "Bulan",
      CalendarViewMode.AGENDA to "Agenda",
      CalendarViewMode.HOLIDAYS to "Hari Libur"
    )

    PrimaryTabRow(
      selectedTabIndex = tabs.indexOfFirst { it.first == currentViewMode }.coerceAtLeast(0),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("calendar_tabs")
    ) {
      tabs.forEach { (mode, title) ->
        val selected = currentViewMode == mode
        Tab(
          selected = selected,
          onClick = { onViewModeChanged(mode) },
          text = {
            Text(
              text = title,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
              fontSize = 14.sp
            )
          }
        )
      }
    }
  }
}
