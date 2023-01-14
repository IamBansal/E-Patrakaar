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
import com.example.e_patrakaar.databinding.CustomNewsItemBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.OnItemClickListener

class CustomNewsAdapter(
    private val fragment: Fragment,
    private val list: List<Collection>,
    private val clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<CustomNewsAdapter.ViewHolder>() {
    private val newsList = ArrayList<Collection>()

    class ViewHolder(view: CustomNewsItemBinding) : RecyclerView.ViewHolder(view.root) {
        val title: TextView = view.textView
        val time: TextView = view.time
        val image: ImageView = view.image
        val card: ConstraintLayout = view.card
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomNewsItemBinding.inflate(
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
        holder.card.setOnClickListener {
            clickListener.onItemClick(news)
        }
        Glide.with(fragment).load(news.image).centerCrop().into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: ArrayList<Collection>) {
        val diffCallBack = NewsCallBack(newsList, list)
        val diffNews = DiffUtil.calculateDiff(diffCallBack)
        newsList.clear()
        newsList.addAll(list)
        diffNews.dispatchUpdatesTo(this)
    }
}