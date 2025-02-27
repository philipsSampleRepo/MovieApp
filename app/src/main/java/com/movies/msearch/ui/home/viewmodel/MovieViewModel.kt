package com.movies.msearch.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.msearch.data.domain.usecases.SearchMoviesUseCase
import com.movies.msearch.data.network.ApiResponse
import com.movies.msearch.data.network.NoInternetException
import com.movies.msearch.data.response.MovieResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _moviesState = MutableSharedFlow<ApiResponse<MovieResponse>>()
    val moviesState: SharedFlow<ApiResponse<MovieResponse>> get() = _moviesState

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                _moviesState.emit(ApiResponse.Loading)
                _moviesState.emit(searchMoviesUseCase(query))
            } catch (e: NoInternetException) {
                _moviesState.emit(ApiResponse.Error("No internet connection. Please try again later."))
            } catch (e: Exception) {
                _moviesState.emit(ApiResponse.Error("No internet connection. Please try again later."))
            }
        }
    }
}