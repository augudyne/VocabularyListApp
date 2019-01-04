package com.projects.valerian.vocabularylist.singletons

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.projects.valerian.vocabularylist.models.User
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStore @Inject() constructor(){

    private var user: User? = null

    fun isLoggedIn(): Boolean = user?.bearerExpiry?.isExpired() == false

    fun getUser(context: Context): User? {
        Log.d(TAG, "Fetching user from user store")
        if (user == null) {
            val sharedPreferences = context.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE)

            val user: User? = sharedPreferences.getString(SHARED_PREFERENCE_KEY_USER, null)?.let {
                Gson().fromJson(it, User::class.java)
            }

            Log.d(TAG, "No logged in user, shared preferences found: $user")

            user?.let {
                if (!it.bearerExpiry.isExpired()) {
                    saveUser(it, context)
                }
            }
        } else if (user?.bearerExpiry?.isExpired() == true) {
            this.user = null
        }

        return this.user
    }

    fun clearUser(context: Context) {
        this.user = null
        context.getSharedPreferences(USER_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().apply {
            remove(SHARED_PREFERENCE_KEY_USER)
        }.apply()
    }

    fun setUser(user: User, context: Context) {
        saveUser(user, context)
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