package com.example.e_patrakaar.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_patrakaar.databinding.CustomRecommendedItemBinding
import com.example.e_patrakaar.model.Collection

class RecommendedAdapter(private val fragment: Fragment, private val list: List<Collection>) :
    RecyclerView.Adapter<RecommendedAdapter.ViewHolder>() {
    private val newsList = ArrayList<Collection>()

    class ViewHolder(view: CustomRecommendedItemBinding) : RecyclerView.ViewHolder(view.root) {
        val text: TextView = view.textView
        val image: ImageView = view.image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomRecommendedItemBinding.inflate(
                LayoutInflater.from(fragment.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = list[position]
        holder.text.text = news.title
        Glide.with(fragment).load(news.image).centerCrop().into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(list: ArrayList<Collection>) {
        val diffCallBack = NewsCallBack(newsList, list)
        val diffNews = DiffUtil.calculateDiff(diffCallBack)
        newsList.clear()
        newsList.addAll(list)
        diffNews.dispatchUpdatesTo(this)
    }
}