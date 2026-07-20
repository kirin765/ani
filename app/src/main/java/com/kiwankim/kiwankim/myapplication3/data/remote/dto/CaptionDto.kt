package com.kiwankim.kiwankim.myapplication3.data.remote.dto

import com.kiwankim.kiwankim.myapplication3.domain.Caption
import kotlinx.serialization.Serializable

@Serializable
data class CaptionDto(
    val episode: String = "",
    val updDt: String = "",
    val website: String = "",
    val name: String = "",
) {
    fun toDomain() = Caption(
        episode = episode,
        updatedAt = updDt.replace('T', ' '),
        website = website,
        name = name,
    )
}
