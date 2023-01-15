package com.example.e_patrakaar.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.CutsomNotificationItemBinding
import com.example.e_patrakaar.model.Notification

class NotificationAdapter(private val fragment: Fragment, list: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var notificationList: List<Notification> = list

    class ViewHolder(view: CutsomNotificationItemBinding) : RecyclerView.ViewHolder(view.root) {
        val notificationMessage: TextView = view.textView
        val categoryNTime: TextView = view.categoryTime
        val image: ImageView = view.image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CutsomNotificationItemBinding.inflate(
                LayoutInflater.from(fragment.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.notificationMessage.text = notification.notificationMessage
        holder.categoryNTime.text = "${notification.category}, ${notification.uploadTime}"
        Glide.with(fragment).load(R.drawable.cityfour).circleCrop()
            .placeholder(R.drawable.comment_outline).into(holder.image)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    fun setData(list: List<Notification>) {
        val diffCallBack = NotificationCallBack(notificationList, list)
        val diffNews = DiffUtil.calculateDiff(diffCallBack)
        notificationList = list
        diffNews.dispatchUpdatesTo(this)
    }

    class NotificationCallBack(
        private val oldList: List<Notification>,
        private val newList: List<Notification>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].notificationMessage == newList[newItemPosition].notificationMessage
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val content1 = oldList[oldItemPosition].notificationMessage
            val upload1 = oldList[oldItemPosition].uploadTime
            val content2 = newList[newItemPosition].notificationMessage
            val upload2 = newList[newItemPosition].uploadTime
            return content1 == content2 && upload1 == upload2
        }
    }
}