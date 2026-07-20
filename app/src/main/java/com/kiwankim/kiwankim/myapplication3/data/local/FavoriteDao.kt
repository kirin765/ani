package com.kiwankim.kiwankim.myapplication3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteAnime>>

    @Query("SELECT animeNo FROM favorites")
    fun observeIds(): Flow<List<Int>>

    @Query("SELECT * FROM favorites")
    suspend fun getAll(): List<FavoriteAnime>

    @Query("SELECT * FROM favorites WHERE animeNo = :animeNo")
    suspend fun get(animeNo: Int): FavoriteAnime?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(favorite: FavoriteAnime)

    @Query("UPDATE favorites SET notify = :notify WHERE animeNo = :animeNo")
    suspend fun setNotify(animeNo: Int, notify: Boolean)

    @Query("DELETE FROM favorites WHERE animeNo = :animeNo")
    suspend fun delete(animeNo: Int)
}
