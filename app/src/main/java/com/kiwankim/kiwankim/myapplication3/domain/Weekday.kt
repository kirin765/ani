package com.kiwankim.kiwankim.myapplication3.domain

import java.util.Calendar

/** Anissia schedule day identifiers. 0-6 map to Sun-Sat, 7=기타, 8=신작. */
enum class Weekday(val code: Int, val label: String) {
    SUN(0, "일"),
    MON(1, "월"),
    TUE(2, "화"),
    WED(3, "수"),
    THU(4, "목"),
    FRI(5, "금"),
    SAT(6, "토"),
    OTHER(7, "기타"),
    NEW(8, "신작");

    companion object {
        /** Tabs shown in the schedule screen, in display order. */
        val tabs: List<Weekday> = listOf(MON, TUE, WED, THU, FRI, SAT, SUN, NEW, OTHER)

        fun today(): Weekday {
            // Calendar.SUNDAY = 1 .. SATURDAY = 7  ->  code 0..6
            val dow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            return entries.first { it.code == dow - 1 }
        }

        fun fromCode(code: Int): Weekday = entries.firstOrNull { it.code == code } ?: OTHER
    }
}
