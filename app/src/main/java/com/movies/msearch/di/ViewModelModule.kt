package com.movies.msearch.di

import androidx.lifecycle.ViewModelProvider
import com.movies.msearch.ui.home.viewmodel.MovieViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: MovieViewModelFactory): ViewModelProvider.Factory
}