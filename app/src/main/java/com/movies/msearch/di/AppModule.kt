package com.movies.msearch.di

import android.content.Context
import com.movies.msearch.data.domain.usecases.SearchMoviesUseCase
import com.movies.msearch.data.network.MovieApiService
import com.movies.msearch.data.network.RetrofitClient
import com.movies.msearch.data.repository.MovieRepository
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun provideMovieApiService(context:Context): MovieApiService = RetrofitClient.create(context)

    @Provides
    fun provideMovieRepository(apiService: MovieApiService): MovieRepository =
        MovieRepository(apiService)

    @Provides
    fun provideSearchMoviesUseCase(repository: MovieRepository): SearchMoviesUseCase =
        SearchMoviesUseCase(repository)
}