package com.kiwankim.kiwankim.myapplication3.di

import android.content.Context
import androidx.room.Room
import com.kiwankim.kiwankim.myapplication3.data.AnimeRepository
import com.kiwankim.kiwankim.myapplication3.data.local.AppDatabase
import com.kiwankim.kiwankim.myapplication3.data.local.FavoriteDao
import com.kiwankim.kiwankim.myapplication3.data.remote.AnissiaApi
import com.kiwankim.kiwankim.myapplication3.notification.AiringScheduler
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/** Manual dependency container held by [com.kiwankim.kiwankim.myapplication3.AniApplication]. */
class AppContainer(context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttp = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val api: AnissiaApi = Retrofit.Builder()
        .baseUrl(AnissiaApi.BASE_URL)
        .client(okHttp)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(AnissiaApi::class.java)

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "anitime.db",
    ).build()

    val favoriteDao: FavoriteDao = database.favoriteDao()

    val airingScheduler = AiringScheduler(context)

    val repository = AnimeRepository(api, favoriteDao, airingScheduler)
}
