package com.projects.valerian.vocabularylist.singletons

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.projects.valerian.vocabularylist.apis.WordsApi
import com.projects.valerian.vocabularylist.models.User
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStore @Inject() constructor(){

    @Inject
    internal lateinit var wordsApi: WordsApi

    val userStatusSubject: PublishSubject<UserState> = PublishSubject.create()

    sealed class UserState {
        class SignedIn(user: User): UserState()
        object SignedOut: UserState()
    }

    private var wordsDisposable: Disposable? = null

    private var user: User? = null
    set(value) {
        field = value
        if (value == null) {
            userStatusSubject.onNext(UserState.SignedOut)
        } else {
            userStatusSubject.onNext(UserState.SignedIn(value))
        }
    }

    fun isLoggedIn(context: Context) : Boolean = this.user != null || getUserFromSharedPrefs(context) != null

    fun getUser(context: Context) = if (this.user != null) this.user else getUserFromSharedPrefs(context)

    fun clearUser(context: Context) {
        this.user = null
        context.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().apply {
            remove(SHARED_PREFERENCE_KEY_USER)
        }.apply()
    }

    fun setUser(user: User, context: Context) {
        saveUser(user, context)
    }

    private fun getUserFromSharedPrefs(context: Context): User? {
        val sharedPreferences = context.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        val user: User? = sharedPreferences.getString(SHARED_PREFERENCE_KEY_USER, null)?.let {
            Gson().fromJson(it, User::class.java)
        }

        Log.d(TAG, "No logged in user, shared preferences found: $user")

        user?.let {
            saveUser(it, context)
        } ?: run {
            this.user = null
        }

        return user
    }

    private fun saveUser(user: User, context: Context) {
        this.user = user
        val sharedPreferences = context.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        this.user?.let {
            // update the shared preferences
            Log.d(TAG, "Saving $user to SharedPreferences")
            sharedPreferences.edit().apply {
                putString(SHARED_PREFERENCE_KEY_USER, Gson().toJson(it, User::class.java))
            }.apply()
        }
    }

    private fun Long.isExpired() = this <= Calendar.getInstance().timeInMillis

    companion object {
        const val USER_SHARED_PREFERENCES = "user_shared_preferences"
        const val SHARED_PREFERENCE_KEY_USER = "shared_preference_key_user"
        const val TAG = "UserStore"
    }
}