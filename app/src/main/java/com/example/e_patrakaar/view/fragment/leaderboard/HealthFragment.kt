package com.example.e_patrakaar.view.fragment.leaderboard

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.e_patrakaar.databinding.FragmentHealthBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.adapter.CustomNewsAdapter
import com.example.e_patrakaar.view.adapter.LatestTechAdapter
import com.example.e_patrakaar.viewmodel.RandomNewsViewModel

class HealthFragment : Fragment() {

    private lateinit var binding: FragmentHealthBinding
    private lateinit var list: ArrayList<Collection>

    private lateinit var randomNewsViewModel: RandomNewsViewModel
    private lateinit var progressBar: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHealthBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        randomNewsViewModel = ViewModelProvider(this)[RandomNewsViewModel::class.java]
        randomNewsViewModel.getNewsFromAPI()
        progressBar = ProgressDialog(requireActivity())
        progressBar.setMessage("Loading news..")
        progressBar.show()

        list = ArrayList()
        randomNewsViewModelObserver()

    }

    private fun randomNewsViewModelObserver() {

        randomNewsViewModel.randomNewsResponse.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                val random = (0..50).random()
                for (i in 0 until it.articles.size){
                    val e = it.articles[i]
                    list.add(Collection(e.article,e.discription,e.image))
                    setResponseInUI(list)
                }
                progressBar.dismiss()
            }
        }

        randomNewsViewModel.randomNewsLoadingError.observe(
            viewLifecycleOwner
        ){
            it?.let {

            }
        }
        randomNewsViewModel.loadRandomNews.observe(
            viewLifecycleOwner
        ){
            it?.let {
                if (it){
                    progressBar.show()
                } else {
                    progressBar.dismiss()
                }
            }
        }

    }

    private fun setResponseInUI(list: ArrayList<Collection>) {
        binding.rvHealthTop.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        binding.rvHealthTop.adapter = CustomNewsAdapter(this@HealthFragment, list)

        binding.rvLatestTechnology.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        binding.rvLatestTechnology.adapter = LatestTechAdapter(this@HealthFragment, list)
    }

}