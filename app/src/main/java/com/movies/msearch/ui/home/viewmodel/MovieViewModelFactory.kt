package com.movies.msearch.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movies.msearch.data.domain.usecases.SearchMoviesUseCase
import javax.inject.Inject

class MovieViewModelFactory @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieViewModel(searchMoviesUseCase) as T
    }
}
