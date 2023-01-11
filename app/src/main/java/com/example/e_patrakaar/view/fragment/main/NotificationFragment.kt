package com.example.e_patrakaar.view.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.FragmentNotificationBinding
import com.example.e_patrakaar.model.Notification
import com.example.e_patrakaar.view.activity.MainActivity
import com.example.e_patrakaar.view.adapter.NotificationAdapter

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var adapter: NotificationAdapter

    companion object {
        fun dummyList(): MutableList<Notification> {
            return mutableListOf(
                Notification(R.drawable.cityone, "New news added.", "Sports", "1 min ago."),
                Notification(R.drawable.twelve, "New news added.", "Sports", "1 min ago."),
                Notification(R.drawable.citytwo, "New news added.", "Sports", "1 min ago."),
                Notification(R.drawable.citythree, "Old news added.", "Sports", "1 min ago."),
                Notification(R.drawable.eleven, "New news added.", "Sports", "1 min ago."),
                Notification(R.drawable.nine, "Old news added.", "Sports", "1 min ago."),
                Notification(R.drawable.cityfour, "New news added.", "Sports", "1 min ago."),
                Notification(R.drawable.ten, "New news added.", "Sports", "1 min ago."),
                Notification(R.drawable.eleven, "New news added.", "Sports", "1 min ago."),
                Notification(R.drawable.nine, "Old news added.", "Sports", "1 min ago."),
                Notification(R.drawable.cityfour, "New news added.", "Sports", "1 min ago."),
                Notification(R.drawable.ten, "New news added.", "Sports", "1 min ago."),
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotificationAdapter(this@NotificationFragment, dummyList())
        binding.rvNotifications.adapter = adapter
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireActivity())

        (context as MainActivity).list.observe(viewLifecycleOwner){
            for(notification in it){
                Log.d("Notification" , "${notification.notificationMessage} ${notification.category}")
            }
            adapter.setData(it)
        }
    }

    fun setList(list: List<Notification>) {
        adapter.setData(list)
    }
}