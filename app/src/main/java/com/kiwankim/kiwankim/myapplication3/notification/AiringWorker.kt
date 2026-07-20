package com.kiwankim.kiwankim.myapplication3.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kiwankim.kiwankim.myapplication3.AniApplication

class AiringWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val animeNo = inputData.getInt(KEY_ANIME_NO, -1)
        if (animeNo == -1) return Result.failure()

        // Only fire while the anime is still favorited with notifications on.
        val dao = (applicationContext as AniApplication).container.favoriteDao
        val fav = dao.get(animeNo)
        if (fav == null || !fav.notify) return Result.success()

        val subject = inputData.getString(KEY_SUBJECT).orEmpty()
        val time = inputData.getString(KEY_TIME).orEmpty()
        AiringNotifier.notifyAiring(applicationContext, animeNo, subject, time)

        // Re-arm for next week using the latest stored data.
        val delay = AiringScheduler.delayToNextAiring(fav.weekCode, fav.time)
        if (delay != null) {
            (applicationContext as AniApplication).container.airingScheduler.enqueue(fav, delay)
        }
        return Result.success()
    }

    companion object {
        const val KEY_ANIME_NO = "animeNo"
        const val KEY_SUBJECT = "subject"
        const val KEY_TIME = "time"
        const val KEY_WEEK = "week"
    }
}
