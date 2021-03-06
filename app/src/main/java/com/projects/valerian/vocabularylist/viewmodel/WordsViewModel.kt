package com.projects.valerian.vocabularylist.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.projects.valerian.vocabularylist.apis.WordsApi
import com.projects.valerian.vocabularylist.models.NoActiveUserException
import com.projects.valerian.vocabularylist.models.Word
import com.projects.valerian.vocabularylist.singletons.UserStore
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordsViewModel @Inject() constructor(private val userStore: UserStore) : ViewModel() {

    @Inject
    internal lateinit var wordsApi: WordsApi

    private var words: MutableList<Word> = mutableListOf()
        set(value) {
            field.clear()
            field.addAll(value)
            field.sortBy { it.id }
            wordsSubject.onNext(field)
        }

    val wordsSubject: PublishSubject<List<Word>> = PublishSubject.create()

    /**
     * Save words to offline cache with shared preferences
     */
    fun cacheWords(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_WORDS, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString(SHARED_PREFERENCE_KEY_WORDS, Gson().toJson(words))
        }.apply()
    }

    /**
     * Adds the given word to the currently logged in user
     */
    fun addWord(word: String, context: Context): Single<List<Word>> =
        userStore.getUser(context)?.let { user ->
            searchWord(word)
                .flatMap { wordsApi.addWord(word, user.bearerToken) }
                .map {
                    words = it.toMutableList()
                    it
                }
        } ?: Single.error<List<Word>>(NoActiveUserException())

    /**
     * Deletes the given word to the currently logged in user
     */
    fun deleteWord(word: String, context: Context): Single<List<Word>> =
        userStore.getUser(context)?.let { user ->
            searchWord(word)
                .flatMap { wordsApi.deleteWord(word, user.bearerToken) }
                .map {
                    words = it.toMutableList()
                    it
                }
        } ?: Single.error<List<Word>>(NoActiveUserException())

    /**
     * Retrieve the words for the current user (handles not logged in)
     * @param forceFetch force an API call
     * @return Empty if no active user, otherwise will return a maybe of a list of words for the user
     */
    fun getWordsForUser(context: Context, forceFetch: Boolean = false): Maybe<List<Word>> {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_WORDS, Context.MODE_PRIVATE)
        val cachedords = Gson().fromJson<List<Word>>(sharedPreferences.getString(SHARED_PREFERENCE_KEY_WORDS, ""), List::class.java)

        return if (forceFetch) {
            Log.d("WordsViewModel", "Forcing fetch...")
            userStore.getUser(context)?.let { user ->
                Log.d(TAG, "User is defined, getting with words")
                wordsApi.getAllWords(user.bearerToken)
                    .toMaybe()
                    .map { words = it.toMutableList(); it}
            } ?: Maybe.just(cachedords.toList())
        } else Maybe.just(cachedords.toList())
    }

    fun getWordCountForUser(forceFetch: Boolean = false): Int = words.size

    fun getWordAtPosition(position: Int): Word? = if (position < words.size) {
        words[position]
    } else {
        Log.e(TAG, "No word at position $position in view model.")
        null
    }

    /**
     * Search for the given word with an API call, returning a Maybe for a word
     */
    private fun searchWord(word: String): Single<Word> = wordsApi.searchWord(word)

    companion object {
        private const val TAG = "WordsViewModel"

        private const val SHARED_PREFERENCE_WORDS = "shared_preference_words"
        private const val SHARED_PREFERENCE_KEY_WORDS = "shared_preference_key_words"
    }
}