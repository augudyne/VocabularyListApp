package com.projects.valerian.vocabularylist.apis

import com.projects.valerian.vocabularylist.models.LoginData
import com.projects.valerian.vocabularylist.models.LoginResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {
    @POST("account/login")
    fun login(@Body loginData: LoginData): Single<LoginResponse>

    @POST("account/register")
    fun register(@Body signupData: RegisterData): Single<Any>

    class RegisterData(val firstName: String, val email: String, val username: String, val password: String)
}