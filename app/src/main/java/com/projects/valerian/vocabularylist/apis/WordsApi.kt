package com.projects.valerian.vocabularylist.apis

import com.projects.valerian.vocabularylist.models.Word
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header

interface WordsApi {
    @GET("words")
    fun getAllWords(@Header("X-Auth-Token") bearerToken: String): Single<List<Word>>
}