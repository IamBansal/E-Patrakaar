package com.example.e_patrakaar.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.e_patrakaar.model.Collection

/**
 * CallBack for calculating the difference between two lists
 */
class NewsCallBack(
    private val oldList: ArrayList<Collection>,
    private val newList: ArrayList<Collection>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].url == newList[newItemPosition].url
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val title1 = oldList[oldItemPosition].title
        val description1 = oldList[oldItemPosition].description
        val title2 = newList[newItemPosition].title
        val description2 = newList[newItemPosition].description
        return title1 == title2 && description1 == description2
    }
}