package com.example.e_patrakaar.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_patrakaar.databinding.LatestTechItemBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.OnItemClickListener

class LatestTechAdapter(
    private val fragment: Fragment,
    private val list: List<Collection>,
    private val clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<LatestTechAdapter.ViewHolder>() {
    private val newsList = ArrayList<Collection>()

    class ViewHolder(view: LatestTechItemBinding) : RecyclerView.ViewHolder(view.root) {
        val title: TextView = view.textView
        val time: TextView = view.time
        val image: ImageView = view.image
        val card: RelativeLayout = view.card
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LatestTechItemBinding.inflate(
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
        holder.card.setOnClickListener {
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