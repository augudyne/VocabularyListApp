package com.projects.valerian.vocabularylist.apis

import com.projects.valerian.vocabularylist.models.LoginData
import com.projects.valerian.vocabularylist.models.LoginResponse
import com.projects.valerian.vocabularylist.models.User
import com.projects.valerian.vocabularylist.singletons.UserStore
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {
    @POST("account/login")
    fun login(@Body loginData: LoginData): Single<LoginResponse>
}