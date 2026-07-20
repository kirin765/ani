package com.kiwankim.kiwankim.myapplication3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteAnime(
    @PrimaryKey val animeNo: Int,
    val subject: String,
    val time: String,
    val genres: String,
    val weekCode: Int,
    /** Whether to fire a broadcast reminder for this anime. */
    val notify: Boolean = true,
    val addedAt: Long,
)
