package com.movies.msearch.di

import com.movies.msearch.ui.activity.MainActivity
import com.movies.msearch.ui.home.fragments.MovieListFragment
import dagger.Component

@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: MovieListFragment)
}