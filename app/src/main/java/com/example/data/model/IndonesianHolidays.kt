package com.example.data.model

data class IndonesianHoliday(
  val date: String, // YYYY-MM-DD
  val name: String,
  val isNationalHoliday: Boolean = true, // Tanggal merah
  val description: String = ""
)

object IndonesianHolidays {

  // Comprehensive Indonesian national holidays
  private val HOLIDAYS = listOf(
    // 2025
    IndonesianHoliday("2025-01-01", "Tahun Baru 2025 Masehi"),
    IndonesianHoliday("2025-01-27", "Isra Mi'raj Nabi Muhammad SAW"),
    IndonesianHoliday("2025-01-29", "Tahun Baru Imlek 2576 Kongzili"),
    IndonesianHoliday("2025-03-29", "Hari Suci Nyepi (Tahun Baru Saka 1947)"),
    IndonesianHoliday("2025-03-31", "Hari Raya Idul Fitri 1446 H"),
    IndonesianHoliday("2025-04-01", "Hari Raya Idul Fitri 1446 H"),
    IndonesianHoliday("2025-04-18", "Wafat Yesus Kristus (Jumat Agung)"),
    IndonesianHoliday("2025-04-20", "Hari Paskah"),
    IndonesianHoliday("2025-05-01", "Hari Buruh Internasional"),
    IndonesianHoliday("2025-05-12", "Hari Raya Waisak 2569 BE"),
    IndonesianHoliday("2025-05-29", "Kenaikan Yesus Kristus"),
    IndonesianHoliday("2025-06-01", "Hari Lahir Pancasila"),
    IndonesianHoliday("2025-06-06", "Hari Raya Idul Adha 1446 H"),
    IndonesianHoliday("2025-06-27", "Tahun Baru Islam 1447 H"),
    IndonesianHoliday("2025-08-17", "Hari Proklamasi Kemerdekaan RI ke-80"),
    IndonesianHoliday("2025-09-05", "Maulid Nabi Muhammad SAW"),
    IndonesianHoliday("2025-12-25", "Hari Raya Natal"),

    // 2026
    IndonesianHoliday("2026-01-01", "Tahun Baru 2026 Masehi"),
    IndonesianHoliday("2026-01-16", "Isra Mi'raj Nabi Muhammad SAW"),
    IndonesianHoliday("2026-02-17", "Tahun Baru Imlek 2577 Kongzili"),
    IndonesianHoliday("2026-03-19", "Hari Suci Nyepi (Tahun Baru Saka 1948)"),
    IndonesianHoliday("2026-03-20", "Hari Raya Idul Fitri 1447 H"),
    IndonesianHoliday("2026-03-21", "Hari Raya Idul Fitri 1447 H"),
    IndonesianHoliday("2026-04-03", "Wafat Yesus Kristus"),
    IndonesianHoliday("2026-04-05", "Hari Paskah"),
    IndonesianHoliday("2026-05-01", "Hari Buruh Internasional"),
    IndonesianHoliday("2026-05-14", "Kenaikan Yesus Kristus"),
    IndonesianHoliday("2026-05-31", "Hari Raya Waisak 2570 BE"),
    IndonesianHoliday("2026-06-01", "Hari Lahir Pancasila"),
    IndonesianHoliday("2026-05-27", "Hari Raya Idul Adha 1447 H"),
    IndonesianHoliday("2026-06-16", "Tahun Baru Islam 1448 H"),
    IndonesianHoliday("2026-08-17", "Hari Proklamasi Kemerdekaan RI ke-81"),
    IndonesianHoliday("2026-08-25", "Maulid Nabi Muhammad SAW"),
    IndonesianHoliday("2026-10-28", "Hari Sumpah Pemuda", isNationalHoliday = false),
    IndonesianHoliday("2026-11-10", "Hari Pahlawan", isNationalHoliday = false),
    IndonesianHoliday("2026-12-25", "Hari Raya Natal"),

    // 2027
    IndonesianHoliday("2027-01-01", "Tahun Baru 2027 Masehi"),
    IndonesianHoliday("2027-02-06", "Tahun Baru Imlek 2578"),
    IndonesianHoliday("2027-03-10", "Hari Raya Idul Fitri 1448 H"),
    IndonesianHoliday("2027-03-11", "Hari Raya Idul Fitri 1448 H"),
    IndonesianHoliday("2027-03-26", "Wafat Yesus Kristus"),
    IndonesianHoliday("2027-05-01", "Hari Buruh Internasional"),
    IndonesianHoliday("2027-05-06", "Kenaikan Yesus Kristus"),
    IndonesianHoliday("2027-06-01", "Hari Lahir Pancasila"),
    IndonesianHoliday("2027-08-17", "Hari Kemerdekaan RI"),
    IndonesianHoliday("2027-12-25", "Hari Raya Natal")
  )

  fun getHolidayForDate(dateStr: String): IndonesianHoliday? {
    return HOLIDAYS.firstOrNull { it.date == dateStr }
  }

  fun getHolidaysForYear(year: Int): List<IndonesianHoliday> {
    val prefix = "$year-"
    return HOLIDAYS.filter { it.date.startsWith(prefix) }
  }

  fun getHolidaysForMonth(year: Int, month: Int): List<IndonesianHoliday> {
    val prefix = String.format(java.util.Locale.ROOT, "%04d-%02d", year, month)
    return HOLIDAYS.filter { it.date.startsWith(prefix) }
  }
}
