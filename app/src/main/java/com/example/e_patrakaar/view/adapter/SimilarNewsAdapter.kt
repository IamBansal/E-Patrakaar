package com.example.e_patrakaar.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_patrakaar.databinding.RvSimilarNewsItemBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.OnItemClickListener

class SimilarNewsAdapter(
    private val fragment: Fragment,
    private val list: List<Collection>,
    val clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<SimilarNewsAdapter.ViewHolder>() {
    private val newsList = ArrayList<Collection>()

    class ViewHolder(view: RvSimilarNewsItemBinding) : RecyclerView.ViewHolder(view.root) {
        val title: TextView = view.tvSimilarNewsTitle
        val time: TextView = view.tvSimilarNewsTime
        val image: ImageView = view.ivSimilarNewsItem
        val item: ConstraintLayout = view.newsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvSimilarNewsItemBinding.inflate(
                LayoutInflater.from(fragment.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = list[position]
        holder.title.text = news.title
        holder.time.text = news.publishedAt
        Glide.with(fragment).load(news.image).centerCrop().into(holder.image)
        holder.item.setOnClickListener {
            clickListener.onItemClick(news)
        }
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