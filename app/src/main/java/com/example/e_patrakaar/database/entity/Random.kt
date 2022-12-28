package com.example.e_patrakaar.database.entity

object Random {

    data class News(
        val articles: List<NewsR>,
//        val status: String,
//        val totalResults: Int
    )

    data class NewsR(
        val id: Int,
        val category: String,
        val article: String,
        val discription: String,
        val time: String,
        val source: String,
        val image: String
    )

}