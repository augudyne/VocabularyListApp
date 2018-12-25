package com.projects.valerian.vocabularylist.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.projects.valerian.vocabularylist.viewmodel.WordsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WordsViewModel::class)
    internal abstract fun wordsViewModel(viewModel: WordsViewModel): ViewModel
}