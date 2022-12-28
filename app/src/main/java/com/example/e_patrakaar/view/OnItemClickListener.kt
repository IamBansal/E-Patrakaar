package com.example.e_patrakaar.view

import androidx.recyclerview.widget.RecyclerView
import com.example.e_patrakaar.model.Collection

/**
 * Interface for recycler view item click listener
 */
interface OnItemClickListener {
    fun onItemClick(news: Collection)
    fun onItemClickReturnViewHolder(viewHolder: RecyclerView.ViewHolder)
}