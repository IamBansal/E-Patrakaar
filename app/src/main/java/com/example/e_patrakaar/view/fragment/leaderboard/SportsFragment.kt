package com.example.e_patrakaar.view.fragment.leaderboard

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.e_patrakaar.databinding.FragmentSportsBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.adapter.ChannelAdapter
import com.example.e_patrakaar.view.adapter.CustomNewsAdapter
import com.example.e_patrakaar.view.adapter.RecommendedAdapter
import com.example.e_patrakaar.view.fragment.main.LeaderboardFragmentDirections
import com.example.e_patrakaar.viewmodel.RandomNewsViewModel

class SportsFragment : Fragment() {

    private lateinit var binding: FragmentSportsBinding
    private lateinit var list: ArrayList<Collection>

    private lateinit var randomNewsViewModel: RandomNewsViewModel
    private lateinit var progressBar: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSportsBinding.inflate(inflater, container, false)
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

    //Changes for database
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

    fun newsDetails(news: Collection){
        findNavController().navigate(
            LeaderboardFragmentDirections.actionNavigationLeaderboardToNavigationExpandedNews(
                news
            )
        )
    }

    private fun setResponseInUI(list: ArrayList<Collection>) {
        binding.rvSports1.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        binding.rvSports1.adapter = CustomNewsAdapter(this@SportsFragment, list)

        binding.rvTop.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        binding.rvTop.adapter = ChannelAdapter(this@SportsFragment, list)

        binding.rvRecommendedTeam.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        binding.rvRecommendedTeam.adapter = RecommendedAdapter(this@SportsFragment, list)
    }
}