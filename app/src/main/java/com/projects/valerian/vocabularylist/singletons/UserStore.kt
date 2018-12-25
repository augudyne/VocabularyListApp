package com.projects.valerian.vocabularylist.singletons

import com.projects.valerian.vocabularylist.models.User
import io.reactivex.Maybe
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStore @Inject() constructor(){
    var user: User? = null

    fun isLoggedIn(): Boolean = user != null
}