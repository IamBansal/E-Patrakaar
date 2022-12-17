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
import com.example.e_patrakaar.databinding.CustomViralItemBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.OnItemClickListener

class ViralAdapter(
    private val fragment: Fragment,
    private val list: List<Collection>,
    private val clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<ViralAdapter.ViewHolder>() {
    private val newsList = ArrayList<Collection>()

    class ViewHolder(view: CustomViralItemBinding) : RecyclerView.ViewHolder(view.root) {
        val text: TextView = view.textView
        val image: ImageView = view.image
        val relativeLayout: RelativeLayout = view.parent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomViralItemBinding.inflate(
                LayoutInflater.from(fragment.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viralNews = list[position]
        holder.text.text = viralNews.title
        Glide.with(fragment).load(viralNews.image).centerCrop().into(holder.image)
        holder.relativeLayout.setOnClickListener {
            clickListener.onItemClick(viralNews)
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