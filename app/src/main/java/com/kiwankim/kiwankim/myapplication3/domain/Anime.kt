package com.kiwankim.kiwankim.myapplication3.domain

data class Anime(
    val animeNo: Int,
    val airing: Boolean,
    val time: String,
    val subject: String,
    val originalSubject: String,
    val genres: List<String>,
    val startDate: String,
    val endDate: String,
    val website: String,
    val captionCount: Int,
    val week: Weekday,
) {
    /** true when [time] holds an HH:MM airing time (weekdays 0-6), false for date strings. */
    val hasClockTime: Boolean
        get() = time.length == 5 && time[2] == ':'
}

data class Caption(
    val episode: String,
    val updatedAt: String,
    val website: String,
    val name: String,
)
