package com.example.e_patrakaar.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_patrakaar.database.entity.RandomNews
import com.example.e_patrakaar.database.network.NewsAPIService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomNewsViewModel: ViewModel() {

    private val randomNewsAPIService = NewsAPIService()

    private val compositeDisposable = CompositeDisposable()

    val loadRandomNews = MutableLiveData<Boolean>()
    val randomNewsResponse = MutableLiveData<RandomNews.News>()
    val randomNewsLoadingError = MutableLiveData<Boolean>()

    fun getNewsFromAPI(){
        loadRandomNews.value = true
        compositeDisposable.add(
            randomNewsAPIService.getRandomNews()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :DisposableSingleObserver<RandomNews.News>(){
                    override fun onSuccess(t: RandomNews.News) {
                        try {
                            loadRandomNews.value = true
                            randomNewsResponse.value = t
                            Log.d("check fine", t.articles[0].description)
                            randomNewsLoadingError.value = false
                        }catch (e: Throwable){
                            loadRandomNews.value = false
                            randomNewsLoadingError.value = true
                            Log.d("errorInFetchingDetails", e.toString())
                        }
                    }

                    override fun onError(e: Throwable) {
                        loadRandomNews.value = false
                        randomNewsLoadingError.value = true
                        Log.d("errorInFetchingDetails", e.toString())
                    }

                })
        )
    }

}