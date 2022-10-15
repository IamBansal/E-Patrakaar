package com.example.e_patrakaar.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.e_patrakaar.view.fragment.HealthFragment
import com.example.e_patrakaar.view.fragment.SportsFragment

class LeaderboardPagerViewAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                SportsFragment()
            }
            1 -> {
                HealthFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}