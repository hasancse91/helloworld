package com.example.helloworld.di.module

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.helloworld.data.AppPreference
import com.example.helloworld.data.AppPreferenceImpl
import com.example.helloworld.di.annotations.AppScope
import com.example.helloworld.di.annotations.ApplicationContext
import com.example.helloworld.utils.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {

    @Binds
    @ApplicationContext
    abstract fun provideContext(application: Application): Context

    @Binds
    abstract fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    abstract fun provideAppPreference(preference: AppPreferenceImpl): AppPreference

}