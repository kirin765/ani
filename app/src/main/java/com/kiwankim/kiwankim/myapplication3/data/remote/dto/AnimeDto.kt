package com.kiwankim.kiwankim.myapplication3.data.remote.dto

import com.kiwankim.kiwankim.myapplication3.domain.Anime
import com.kiwankim.kiwankim.myapplication3.domain.Weekday
import kotlinx.serialization.Serializable

@Serializable
data class AnimeDto(
    val animeNo: Int = 0,
    val status: String = "OFF",
    val time: String = "",
    val subject: String = "",
    val originalSubject: String = "",
    val genres: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val website: String = "",
    val captionCount: Int = 0,
) {
    fun toDomain(week: Weekday) = Anime(
        animeNo = animeNo,
        airing = status.equals("ON", ignoreCase = true),
        time = time,
        subject = subject,
        originalSubject = originalSubject,
        genres = genres.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        startDate = startDate,
        endDate = endDate,
        website = website,
        captionCount = captionCount,
        week = week,
    )
}
