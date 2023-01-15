package com.example.e_patrakaar.view.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.FragmentNotificationBinding
import com.example.e_patrakaar.model.Notification
import com.example.e_patrakaar.view.adapter.NotificationAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private lateinit var database: DatabaseReference

    companion object {
        private const val TAG = "NotificationFragment"
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

        database = Firebase.database.reference
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        adapter = NotificationAdapter(this@NotificationFragment, listOf())
        binding.rvNotifications.adapter = adapter
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireActivity())

        val notificationValueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val notificationList = mutableListOf<Notification>()
                val list = snapshot.value as Map<*, *>
                list.forEach { (key , value) ->
                    val hashmap = value as Map<* , *>
                    val notification = Notification()
                    hashmap.forEach { (key1, value1) ->
                        when(key1 as String){
                            "image" -> notification.image = value1
                            "notificationMessage" -> notification.notificationMessage = value1 as String
                            "category" -> notification.category = value1 as String
                            "uploadTime" -> notification.uploadTime = value1 as String
                        }
                    }
                    notificationList.add(notification)
                }
                adapter.setData(notificationList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        if(currentUser!=null){
            database.child("users").child(currentUser).child("notifications")
                .addValueEventListener(notificationValueEventListener)
        }
    }
}