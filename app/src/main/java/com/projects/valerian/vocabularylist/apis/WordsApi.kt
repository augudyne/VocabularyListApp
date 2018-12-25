package com.projects.valerian.vocabularylist.apis

import io.reactivex.Single
import retrofit2.http.GET

interface WordsApi {
    @GET("words")
    fun getAllWords(): Single<Any>

}