package com.kiwankim.kiwankim.myapplication3.data

import com.kiwankim.kiwankim.myapplication3.data.local.FavoriteAnime
import com.kiwankim.kiwankim.myapplication3.data.local.FavoriteDao
import com.kiwankim.kiwankim.myapplication3.data.remote.AnissiaApi
import com.kiwankim.kiwankim.myapplication3.domain.Anime
import com.kiwankim.kiwankim.myapplication3.domain.Caption
import com.kiwankim.kiwankim.myapplication3.domain.Weekday
import com.kiwankim.kiwankim.myapplication3.notification.AiringScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AnimeRepository(
    private val api: AnissiaApi,
    private val favoriteDao: FavoriteDao,
    private val scheduler: AiringScheduler,
    private val now: () -> Long = System::currentTimeMillis,
) {
    private val scheduleCache = mutableMapOf<Int, List<Anime>>()
    private val mutex = Mutex()

    suspend fun schedule(week: Weekday, forceRefresh: Boolean = false): List<Anime> = mutex.withLock {
        if (!forceRefresh) scheduleCache[week.code]?.let { return it }
        val list = api.schedule(week.code).data.orEmpty().map { it.toDomain(week) }
        scheduleCache[week.code] = list
        list
    }

    /** Aggregate every weekday (0-6) plus 신작/기타 for search & genre filtering. */
    suspend fun allAnime(forceRefresh: Boolean = false): List<Anime> {
        val byNo = LinkedHashMap<Int, Anime>()
        for (week in Weekday.entries) {
            schedule(week, forceRefresh).forEach { byNo.putIfAbsent(it.animeNo, it) }
        }
        return byNo.values.toList()
    }

    suspend fun captions(animeNo: Int): List<Caption> =
        api.captions(animeNo).data.orEmpty().map { it.toDomain() }

    /** Look up anime metadata, preferring already-cached schedule data. */
    suspend fun findAnime(animeNo: Int): Anime? {
        scheduleCache.values.flatten().firstOrNull { it.animeNo == animeNo }?.let { return it }
        return allAnime().firstOrNull { it.animeNo == animeNo }
    }

    fun observeFavoriteIds(): Flow<List<Int>> = favoriteDao.observeIds()

    fun observeFavorites(): Flow<List<FavoriteAnime>> = favoriteDao.observeAll()

    suspend fun isFavorite(animeNo: Int): Boolean = favoriteDao.get(animeNo) != null

    suspend fun toggleFavorite(anime: Anime): Boolean {
        val existing = favoriteDao.get(anime.animeNo)
        return if (existing == null) {
            val fav = FavoriteAnime(
                animeNo = anime.animeNo,
                subject = anime.subject,
                time = anime.time,
                genres = anime.genres.joinToString(","),
                weekCode = anime.week.code,
                notify = true,
                addedAt = now(),
            )
            favoriteDao.upsert(fav)
            scheduler.schedule(fav)
            true
        } else {
            favoriteDao.delete(anime.animeNo)
            scheduler.cancel(anime.animeNo)
            false
        }
    }

    suspend fun removeFavorite(animeNo: Int) {
        favoriteDao.delete(animeNo)
        scheduler.cancel(animeNo)
    }

    suspend fun setNotify(animeNo: Int, enabled: Boolean) {
        favoriteDao.setNotify(animeNo, enabled)
        val fav = favoriteDao.get(animeNo) ?: return
        if (enabled) scheduler.schedule(fav) else scheduler.cancel(animeNo)
    }

    /** Re-arm all reminders (used after reboot / app update). */
    suspend fun rescheduleAll() {
        favoriteDao.getAll().filter { it.notify }.forEach { scheduler.schedule(it) }
    }
}
