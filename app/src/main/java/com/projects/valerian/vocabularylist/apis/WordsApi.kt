package com.projects.valerian.vocabularylist.apis

import com.projects.valerian.vocabularylist.models.Word
import io.reactivex.Single
import retrofit2.http.*

interface WordsApi {
    @GET("words")
    fun getAllWords(@Header("X-Auth-Token") bearerToken: String): Single<List<Word>>

    @GET("words/{id}")
    fun searchWord(@Path("id") id: String): Single<Word>

    @PUT("words/{id}")
    fun addWord(@Path("id") id: String, @Header("X-Auth-Token") bearerToken: String): Single<List<Word>>

    @DELETE("words/{id}")
    fun deleteWord(@Path("id") id: String, @Header("X-Auth-Token") bearerToken: String): Single<List<Word>>
}