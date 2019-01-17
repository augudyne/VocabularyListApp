package com.projects.valerian.vocabularylist.dagger

import com.projects.valerian.vocabularylist.LoginActivity
import com.projects.valerian.vocabularylist.MainActivity
import com.projects.valerian.vocabularylist.RegisterActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun contributeRegisterActivity(): RegisterActivity
}