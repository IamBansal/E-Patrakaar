@file:Suppress("DEPRECATION")

package com.example.e_patrakaar.view.fragment.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_patrakaar.databinding.FragmentExpandedNewsBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.OnItemClickListener
import com.example.e_patrakaar.view.WrapContentStaggeredGridLayoutManager
import com.example.e_patrakaar.view.adapter.SimilarNewsAdapter
import com.example.e_patrakaar.viewmodel.RandomNewsViewModel

class ExpandedNewsFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentExpandedNewsBinding
    private lateinit var newsDetails: Collection
    private lateinit var randomNewsViewModel: RandomNewsViewModel
    private lateinit var progressBar: ProgressDialog
    private lateinit var list: ArrayList<Collection>
    private lateinit var adapter: SimilarNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpandedNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()
        setRandomNewsResponseInUI()
        randomNewsViewModel = ViewModelProvider(this)[RandomNewsViewModel::class.java]
        randomNewsViewModel.getNewsFromAPI()
        progressBar = ProgressDialog(context)
        progressBar.setMessage("Loading news..")
        progressBar.show()

        randomNewsViewModelObserver()

    }

    private fun setRandomNewsResponseInUI() {
        val args: ExpandedNewsFragmentArgs by navArgs()
        newsDetails = args.expandedNewsHome
        Glide.with(requireActivity())
            .load(newsDetails.image)
            .centerCrop()
            .into(binding.image)
        binding.title.text = newsDetails.title
        binding.description.text = newsDetails.description
        binding.time.text = newsDetails.publishedAt
        adapter = SimilarNewsAdapter(this, list, this)
        binding.rvSimilarNews.layoutManager =
            WrapContentStaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        binding.rvSimilarNews.adapter = adapter
    }

    private fun randomNewsViewModelObserver() {
        randomNewsViewModel.randomNewsResponse.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                val random = (0..100).random()
                if (it.articles.size >= random + 10) {
                    for (i in random..random + 10) {
//                        val e = it.articles[i]
//                        list.add(Collection(e.title, e.description, e.urlToImage))
                        adapter.setData(list)
                    }
                } else {
                    for (i in random..random + 1) {
//                        val e = it.articles[i]
//                        list.add(Collection(e.title, e.description, e.urlToImage))
                        adapter.setData(list)
                    }
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

    private fun newsDetails(news: Collection) {
        findNavController().navigate(
            ExpandedNewsFragmentDirections.actionNavigationExpandedNewsSelf(
                news
            )
        )
    }

    override fun onItemClick(news: Collection) {
        newsDetails(news)
    }

    override fun onItemClickReturnViewHolder(viewHolder: RecyclerView.ViewHolder) {
        TODO("Not yet implemented")
    }
}