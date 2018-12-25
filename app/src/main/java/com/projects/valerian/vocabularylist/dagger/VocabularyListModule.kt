package com.projects.valerian.vocabularylist.dagger

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.projects.valerian.vocabularylist.apis.WordsApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class VocabularyListModule {

    @Provides
    @Singleton
    fun provideWordsApi(): WordsApi {
        return Retrofit.Builder()
            .baseUrl(BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WordsApi::class.java)
    }

    companion object {
        const val BASE_API_URL = "http://augustinekwong.com/api/"
    }
}