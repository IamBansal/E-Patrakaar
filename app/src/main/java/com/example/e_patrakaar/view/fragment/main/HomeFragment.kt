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
import androidx.recyclerview.widget.RecyclerView
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.FragmentHomeBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.OnItemClickListener
import com.example.e_patrakaar.view.WrapContentStaggeredGridLayoutManager
import com.example.e_patrakaar.view.adapter.*
import com.example.e_patrakaar.viewmodel.RandomNewsViewModel
import kotlin.math.min

class HomeFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var list: ArrayList<Collection>
    private val maxFiveTrendNewsList = ArrayList<Collection>()
    private lateinit var randomNewsViewModel: RandomNewsViewModel
    private lateinit var progressBar: ProgressDialog
    private lateinit var adapterTrending: TrendingNewsAdapter
    private lateinit var adapterUpdates: ViralAdapter
    private lateinit var adapterRecommended: RecommendedAdapter
    private lateinit var adapterChannel: ChannelAdapter
    private lateinit var adapterSuggestedNews: CustomNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()
        setResponseInUI(list)
        randomNewsViewModel = ViewModelProvider(this)[RandomNewsViewModel::class.java]
        randomNewsViewModel.getNewsFromAPI()
        progressBar = ProgressDialog(requireActivity())
        progressBar.setMessage("Loading news..")
        progressBar.show()

        randomNewsViewModelObserver()

    }

    private fun setResponseInUI(list: ArrayList<Collection>) {
        //Dummy list for channels
        val listChannel = ArrayList<Collection>()

        listChannel.add(Collection("", "", R.drawable.nine))
        listChannel.add(Collection("", "", R.drawable.ten))
        listChannel.add(Collection("", "", R.drawable.eleven))
        listChannel.add(Collection("", "", R.drawable.twelve))

        binding.rvTrending.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapterTrending = TrendingNewsAdapter(this@HomeFragment, list, this)
        binding.rvTrending.adapter = adapterTrending
        binding.trendIndicator.attachToRecyclerView(binding.rvTrending)

        binding.rvUpdates.layoutManager =
            WrapContentStaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        adapterUpdates = ViralAdapter(this@HomeFragment, list, this)
        binding.rvUpdates.adapter = adapterUpdates

        binding.rvRecommended.layoutManager =
            WrapContentStaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        adapterRecommended = RecommendedAdapter(this@HomeFragment, list)
        binding.rvRecommended.adapter = adapterRecommended

        binding.rvChannels.layoutManager =
            WrapContentStaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        adapterChannel = ChannelAdapter(this@HomeFragment, listChannel)
        binding.rvChannels.adapter = adapterChannel

        binding.rvSuggestedNews.layoutManager =
            WrapContentStaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        adapterSuggestedNews = CustomNewsAdapter(this, list, this)
        binding.rvSuggestedNews.adapter = adapterSuggestedNews
    }

    private fun newsDetails(news: Collection) {
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
                if (it.articles.size >= random + 10) {
                    for (i in random..random + 10) {
                        val e = it.articles[i]
                        list.add(Collection(e.title, e.description, e.urlToImage))
                    }
                } else {
                    for (i in random..random + 1) {
                        val e = it.articles[i]
                        list.add(Collection(e.title, e.description, e.urlToImage))
                    }
                }
                for (i in 0 until min(5, list.size)) {
                    maxFiveTrendNewsList.add(list.get(i))
                }
                adapterTrending.setData(maxFiveTrendNewsList)
                adapterRecommended.setData(list)
                adapterSuggestedNews.setList(list)
                adapterUpdates.setData(list)
                progressBar.dismiss()
            }
        }

        randomNewsViewModel.randomNewsLoadingError.observe(
            viewLifecycleOwner
        ) {
            it?.let {

            }
        }

        randomNewsViewModel.loadRandomNews.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                if (it) {
                    progressBar.show()
                } else {
                    progressBar.dismiss()
                }
            }
        }

    }

    override fun onItemClick(news: Collection) {
        newsDetails(news)
    }

    override fun onItemClickReturnViewHolder(viewHolder: RecyclerView.ViewHolder) {
    }
}