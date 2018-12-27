package com.projects.valerian.vocabularylist.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.projects.valerian.vocabularylist.apis.WordsApi
import com.projects.valerian.vocabularylist.models.Word
import com.projects.valerian.vocabularylist.singletons.UserStore
import io.reactivex.Maybe
import javax.inject.Inject

class WordsViewModel @Inject() constructor(private val userStore: UserStore) : ViewModel() {

    @Inject
    internal lateinit var wordsApi: WordsApi

    private var hasFetched: Boolean = false
    private var words: MutableList<Word> = mutableListOf()
        set(value) {
            field.clear()
            field.addAll(value)
        }

    /**
     * Retrieve the words for the current user (handles not logged in)
     * @param forceFetch force an API call
     * @return Empty if no active user, otherwise will return a maybe of a list of words for the user
     */
    fun getWordsForUser(context: Context, forceFetch: Boolean = false): Maybe<List<Word>> =
        if (forceFetch) {
            Log.d("WordsViewModel", "Forcing fetch...")
            userStore.getUser(context)?.let { user ->
                Log.d(TAG, "User is defined, getting with words")
                wordsApi.getAllWords(user.bearerToken)
                    .toMaybe()
                    .map { words = it.toMutableList(); it}
            } ?: Maybe.empty<List<Word>>()
        } else Maybe.just(words.toList())

    fun getWordCountForUser(forceFetch: Boolean = false): Int = words.size

    fun getWordAtPosition(position: Int): Word? = if (position < words.size) {
        words[position]
    } else {
        Log.e(TAG, "No word at position $position in view model.")
        null
    }

    companion object {
        private const val TAG = "WordsViewModel"
    }
}