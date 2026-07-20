package com.kiwankim.kiwankim.myapplication3.data.remote

import com.kiwankim.kiwankim.myapplication3.data.remote.dto.AnimeDto
import com.kiwankim.kiwankim.myapplication3.data.remote.dto.ApiResponse
import com.kiwankim.kiwankim.myapplication3.data.remote.dto.CaptionDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AnissiaApi {

    @GET("anime/schedule/{week}")
    suspend fun schedule(@Path("week") week: Int): ApiResponse<List<AnimeDto>>

    @GET("anime/caption/animeNo/{animeNo}")
    suspend fun captions(@Path("animeNo") animeNo: Int): ApiResponse<List<CaptionDto>>

    companion object {
        const val BASE_URL = "https://api.anissia.net/"
    }
}
