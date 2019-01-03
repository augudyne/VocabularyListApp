package com.projects.valerian.vocabularylist.dagger

import com.projects.valerian.vocabularylist.fragments.AddWordDialogFragment
import com.projects.valerian.vocabularylist.fragments.WordsSummaryFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    internal abstract fun contributeWordsSummaryFragment(): WordsSummaryFragment

    @ContributesAndroidInjector
    internal abstract fun contributeAddWordsDialogFragment(): AddWordDialogFragment
}