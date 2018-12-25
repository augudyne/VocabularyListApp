package com.projects.valerian.vocabularylist.dagger

import android.app.Application
import androidx.lifecycle.ViewModel
import com.projects.valerian.vocabularylist.MainActivity
import com.projects.valerian.vocabularylist.VocabularyApplication
import com.projects.valerian.vocabularylist.fragments.WordsSummaryFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * Injection into the main application
 */
@Singleton
@Component(modules = [
    VocabularyListModule::class,
    ViewModelModule::class,
    AndroidInjectionModule::class,
    FragmentModule::class,
    ActivityModule::class])
abstract class VocabularyAppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): VocabularyAppComponent
    }

    abstract fun inject(vocabularyApp: VocabularyApplication)

}