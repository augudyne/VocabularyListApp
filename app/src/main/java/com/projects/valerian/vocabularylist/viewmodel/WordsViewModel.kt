package com.projects.valerian.vocabularylist.viewmodel

import androidx.lifecycle.ViewModel
import com.projects.valerian.vocabularylist.apis.WordsApi
import com.projects.valerian.vocabularylist.models.Word
import com.projects.valerian.vocabularylist.singletons.UserStore
import io.reactivex.Maybe
import javax.inject.Inject

class WordsViewModel @Inject() constructor(private val userStore: UserStore) : ViewModel() {

    @Inject
    internal lateinit var wordsApi: WordsApi

    fun getWordsForUser(): Maybe<List<Word>> =
        userStore.user?.let { user ->
            wordsApi.getAllWords(user.bearerToken).toMaybe()
        } ?: Maybe.empty<List<Word>>()
}