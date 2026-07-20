package com.kiwankim.kiwankim.myapplication3.domain

import androidx.annotation.StringRes
import com.kiwankim.kiwankim.myapplication3.R
import java.util.Calendar

/** Anissia schedule day identifiers. 0-6 map to Sun-Sat, 7=기타, 8=신작. */
enum class Weekday(val code: Int, @StringRes val labelRes: Int) {
    SUN(0, R.string.day_sun),
    MON(1, R.string.day_mon),
    TUE(2, R.string.day_tue),
    WED(3, R.string.day_wed),
    THU(4, R.string.day_thu),
    FRI(5, R.string.day_fri),
    SAT(6, R.string.day_sat),
    OTHER(7, R.string.day_other),
    NEW(8, R.string.day_new);

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
