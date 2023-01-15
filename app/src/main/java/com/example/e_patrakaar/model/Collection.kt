package com.example.e_patrakaar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class Collection(
    var title: String? = null,
    var description: String? = null,
    var image: @RawValue Any? = null,
    val author: String? = null,
    val content: String? = null,
    val publishedAt: String? = null,
    val url: String? = null
) : Parcelable {
    fun toMap(): Map<String , Any?> {
        return mapOf(
            "title" to title,
            "description" to description,
            "image" to image,
            "author" to author,
            "content" to content,
            "publishedAt" to publishedAt,
            "url" to url
        )
    }
}