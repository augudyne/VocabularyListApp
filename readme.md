
# Table of Contents

1.  [Vocabulary List (Android](#orgfe69b42)
    1.  [Setup](#org31bb588)
        1.  [Enable AndroidX in gradle.properties](#org9ba3079)
        2.  [Enable AndroidX lifecycles](#org75c803a)
        3.  [Add RxJava in build.gradle of app](#orgd93dc97)
        4.  [Add Retrofit](#org0f9a339)
        5.  [Setup Dagger2](#orgeb2563e)


<a id="orgfe69b42"></a>

# Vocabulary List (Android


<a id="org31bb588"></a>

## Setup


<a id="org9ba3079"></a>

### Enable AndroidX in gradle.properties

    android.useAndroidX=true
    android.enableJetifier=true 


<a id="org75c803a"></a>

### Enable AndroidX lifecycles

    // ViewModel and LiveData
        implementation "androidx.lifecycle:lifecycle-extensions:$lifecycle_version"
        implementation 'androidx.legacy:legacy-support-v4:1.0.0'
        implementation 'androidx.lifecycle:lifecycle-extensions:2.1.0-alpha01'
        kapt "android.arch.lifecycle:compiler:1.1.1"


<a id="orgd93dc97"></a>

### Add RxJava in build.gradle of app

    implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
    implementation 'io.reactivex.rxjava2:rxjava:2.2.4'
    implementation 'com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0'


<a id="org0f9a339"></a>

### Add Retrofit

`implementation 'com.squareup.retrofit2:converter-gson:2.1.0'`


<a id="orgeb2563e"></a>

### Setup Dagger2

1.  Add dependencies

    // dagger
    kapt "com.google.dagger:dagger-compiler:$dagger_version"
    implementation "com.google.dagger:dagger:$dagger_version"
    
    kapt "com.google.dagger:dagger-android-processor:$dagger_version"
    implementation "com.google.dagger:dagger-android:$dagger_version"
    implementation "com.google.dagger:dagger-android-support:$dagger_version"

1.  Create an application to inject into Activities and Fragments
    Create a class that extends Application and implements HasActivityInjector and HasSupportFragmentInjector. Implement by injecting DispatchingAndroidInjector. In onCreate, build and inject app component
2.  Create AppComponent that will inject into the application. Create @Component.Builder interface. (See VocabularyAppComponent)
3.  Create FragmentModule and Activity module that contributes android injectors
4.  Create other modules e.g. for Retrofit with the builder

