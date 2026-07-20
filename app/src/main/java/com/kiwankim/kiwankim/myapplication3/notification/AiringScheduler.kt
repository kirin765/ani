package com.kiwankim.kiwankim.myapplication3.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kiwankim.kiwankim.myapplication3.data.local.FavoriteAnime
import java.util.Calendar
import java.util.concurrent.TimeUnit

/** Schedules a weekly reminder ~10 minutes before a favorited anime airs. */
class AiringScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context.applicationContext)

    fun schedule(favorite: FavoriteAnime) {
        val delay = delayToNextAiring(favorite.weekCode, favorite.time) ?: run {
            // No usable weekday/time (신작·기타 or date-only) — nothing to schedule.
            cancel(favorite.animeNo)
            return
        }
        enqueue(favorite, delay)
    }

    fun cancel(animeNo: Int) {
        workManager.cancelUniqueWork(workName(animeNo))
    }

    internal fun enqueue(favorite: FavoriteAnime, delayMillis: Long) {
        val data = Data.Builder()
            .putInt(AiringWorker.KEY_ANIME_NO, favorite.animeNo)
            .putString(AiringWorker.KEY_SUBJECT, favorite.subject)
            .putString(AiringWorker.KEY_TIME, favorite.time)
            .putInt(AiringWorker.KEY_WEEK, favorite.weekCode)
            .build()

        val request = OneTimeWorkRequestBuilder<AiringWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(TAG)
            .build()

        workManager.enqueueUniqueWork(workName(favorite.animeNo), ExistingWorkPolicy.REPLACE, request)
    }

    companion object {
        const val TAG = "airing_reminder"
        private const val LEAD_MINUTES = 10

        fun workName(animeNo: Int) = "airing_$animeNo"

        /** Millis from now to the reminder for the next occurrence, or null if not schedulable. */
        fun delayToNextAiring(
            weekCode: Int,
            time: String,
            now: Calendar = Calendar.getInstance(),
        ): Long? {
            if (weekCode !in 0..6) return null
            val hm = time.split(":")
            if (hm.size != 2) return null
            val hour = hm[0].toIntOrNull() ?: return null
            val minute = hm[1].toIntOrNull() ?: return null

            val target = (now.clone() as Calendar).apply {
                // Calendar.DAY_OF_WEEK: SUNDAY=1..SATURDAY=7; weekCode 0..6 maps directly.
                set(Calendar.DAY_OF_WEEK, weekCode + 1)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.MINUTE, -LEAD_MINUTES)
            }
            if (target.timeInMillis <= now.timeInMillis) {
                target.add(Calendar.DAY_OF_YEAR, 7)
            }
            return target.timeInMillis - now.timeInMillis
        }
    }
}
