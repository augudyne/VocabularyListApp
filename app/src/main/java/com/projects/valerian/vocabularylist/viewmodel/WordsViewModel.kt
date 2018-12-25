package com.projects.valerian.vocabularylist.viewmodel

import androidx.lifecycle.ViewModel
import com.projects.valerian.vocabularylist.apis.WordsApi
import io.reactivex.Single
import javax.inject.Inject

class WordsViewModel @Inject() constructor(): ViewModel() {

    @Inject
    internal lateinit var wordsApi: WordsApi

    fun getWordsForUser(): Single<Any> = wordsApi.getAllWords()
}