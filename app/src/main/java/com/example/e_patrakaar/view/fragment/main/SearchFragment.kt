package com.example.e_patrakaar.view.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.FragmentSearchBinding
import com.example.e_patrakaar.model.City
import com.example.e_patrakaar.view.WrapContentStaggeredGridLayoutManager
import com.example.e_patrakaar.view.adapter.CityAdapter

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var list1: List<City>
    private lateinit var list2: List<City>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list1 = listOf(
            City("Mumbai", R.drawable.cityone),
            City("Delhi", R.drawable.citytwo),
            City("Chennai", R.drawable.citythree),
            City("Kolkata", R.drawable.cityfour),
        )
        list2 = listOf(
            City("Mumbai", R.drawable.cityone),
            City("Delhi", R.drawable.citytwo),
            City("Chennai", R.drawable.citythree),
            City("Kolkata", R.drawable.cityfour),
            City("Delhi", R.drawable.citytwo),
            City("Kolkata", R.drawable.cityfour),
            City("Chennai", R.drawable.citythree),
            City("Mumbai", R.drawable.cityone),
        )

        binding.rvCities.layoutManager =
            WrapContentStaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL)

        if (binding.tvViewMore.visibility == View.VISIBLE) {
            binding.rvCities.adapter = CityAdapter(this@SearchFragment, list1)
        } else {
            binding.rvCities.adapter = CityAdapter(this@SearchFragment, list2)
        }

        binding.tvViewLess.setOnClickListener {
            binding.tvViewMore.visibility = View.VISIBLE
            binding.tvViewLess.visibility = View.GONE
            binding.rvCities.adapter = CityAdapter(this@SearchFragment, list1)
        }

        binding.tvViewMore.setOnClickListener {
            binding.tvViewLess.visibility = View.VISIBLE
            binding.tvViewMore.visibility = View.GONE
            binding.rvCities.adapter = CityAdapter(this@SearchFragment, list2)
        }
    }
}