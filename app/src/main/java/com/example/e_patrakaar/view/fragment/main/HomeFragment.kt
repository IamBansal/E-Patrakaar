package com.example.e_patrakaar.view.fragment.main

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.FragmentHomeBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.adapter.ChannelAdapter
import com.example.e_patrakaar.view.adapter.RecommendedAdapter
import com.example.e_patrakaar.view.adapter.TrendingNewsAdapter
import com.example.e_patrakaar.view.adapter.ViralAdapter
import com.example.e_patrakaar.viewmodel.RandomNewsViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var list: ArrayList<Collection>

    private lateinit var randomNewsViewModel: RandomNewsViewModel
    private lateinit var progressBar: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun setResponseInUI(list: ArrayList<Collection>) {

        //Dummy list for channels
        val listChannel = ArrayList<Collection>()
        listChannel.add(Collection("","", R.drawable.nine))
        listChannel.add(Collection("","", R.drawable.ten))
        listChannel.add(Collection("","", R.drawable.eleven))
        listChannel.add(Collection("","", R.drawable.twelve))

        binding.rvTrending.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        binding.rvTrending.adapter = TrendingNewsAdapter(this@HomeFragment, list)

        binding.rvUpdates.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        binding.rvUpdates.adapter = ViralAdapter(this@HomeFragment, list)

        binding.rvRecommended.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        binding.rvRecommended.adapter = RecommendedAdapter(this@HomeFragment, list)

        binding.rvChannels.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        binding.rvChannels.adapter = ChannelAdapter(this@HomeFragment, listChannel)

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

    fun newsDetails(news: Collection){
        findNavController().navigate(
            HomeFragmentDirections.actionNavigationHomeToNavigationExpandedNews(
                news
            )
        )
    }

    //Changes for database
    private fun randomNewsViewModelObserver() {

        randomNewsViewModel.randomNewsResponse.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                val random = (0..100).random()
//                if (it.articles.size >= random + 10){
                for (i in 0 until it.articles.size){
                        val e = it.articles[i]
                        list.add(Collection(e.article,e.discription,e.image))
                        setResponseInUI(list)
                    }
//                } else {
//                    for (i in random..random + 1){
////                        val e = it.articles[i]
//                        list.add(Collection(e.title, e.description, e.urlToImage))
//                        setResponseInUI(list)
//                    }
//                }
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
}