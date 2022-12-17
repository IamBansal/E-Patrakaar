package com.example.e_patrakaar.view.fragment.leaderboard

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
import com.example.e_patrakaar.databinding.FragmentTechnologyBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.OnItemClickListener
import com.example.e_patrakaar.view.WrapContentStaggeredGridLayoutManager
import com.example.e_patrakaar.view.adapter.CustomNewsAdapter
import com.example.e_patrakaar.view.adapter.LatestTechAdapter
import com.example.e_patrakaar.view.fragment.main.LeaderboardFragmentDirections
import com.example.e_patrakaar.viewmodel.RandomNewsViewModel

class TechnologyFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentTechnologyBinding
    private lateinit var list: ArrayList<Collection>
    private lateinit var randomNewsViewModel: RandomNewsViewModel
    private lateinit var progressBar: ProgressDialog
    private lateinit var adapterTechnologyTop: CustomNewsAdapter
    private lateinit var adapterLatestTechnology: LatestTechAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTechnologyBinding.inflate(inflater, container, false)

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

    private fun randomNewsViewModelObserver() {
        randomNewsViewModel.randomNewsResponse.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                val random = (0..50).random()
                for (i in random..random + 5) {
                    val e = it.articles[i]
                    list.add(Collection(e.title, e.description, e.urlToImage))
                    adapterTechnologyTop.setList(list)
                    adapterLatestTechnology.setData(list)
                }
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

    private fun setResponseInUI(list: ArrayList<Collection>) {
        binding.rvTechnologyTop.layoutManager =
            WrapContentStaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        adapterTechnologyTop = CustomNewsAdapter(this@TechnologyFragment, list, this)
        binding.rvTechnologyTop.adapter = adapterTechnologyTop

        binding.rvLatestTechno.layoutManager =
            WrapContentStaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        adapterLatestTechnology = LatestTechAdapter(this@TechnologyFragment, list, this)
        binding.rvLatestTechno.adapter = adapterLatestTechnology
    }

    private fun newsDetails(news: Collection) {
        findNavController().navigate(
            LeaderboardFragmentDirections.actionNavigationLeaderboardToNavigationExpandedNews(
                news
            )
        )
    }

    override fun onItemClick(news: Collection) {
        newsDetails(news)
    }

    override fun onItemClickReturnViewHolder(viewHolder: RecyclerView.ViewHolder) {
    }
}