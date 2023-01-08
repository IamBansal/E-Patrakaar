package com.example.e_patrakaar.database.network

import com.example.e_patrakaar.database.entity.Random
import com.example.e_patrakaar.database.entity.RandomNews
import com.example.e_patrakaar.database.entity.RandomNewsAPI
import com.example.e_patrakaar.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET(Constants.API_ENDPOINT)
    fun getNews(
        @Query(Constants.TAG) tag: String,
        @Query(Constants.API_KEY) apiKey: String,
    ): Single<RandomNews.News>

}