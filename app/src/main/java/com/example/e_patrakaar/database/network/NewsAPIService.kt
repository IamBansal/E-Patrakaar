package com.example.e_patrakaar.database.network

import com.example.e_patrakaar.database.entity.Random
import com.example.e_patrakaar.database.entity.RandomNews
import com.example.e_patrakaar.database.entity.RandomNewsAPI
import com.example.e_patrakaar.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NewsAPIService {

    private val api = Retrofit.Builder().baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(NewsAPI::class.java)

    fun getRandomNews(): Single<RandomNewsAPI> {
        return api.getNews(
//            Constants.TAG_VALUE,
//            Constants.API_KEY_VALUE,
//            Constants.NUMBER_VALUE
        )
    }
}