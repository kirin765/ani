package com.kiwankim.kiwankim.myapplication3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteAnime::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
