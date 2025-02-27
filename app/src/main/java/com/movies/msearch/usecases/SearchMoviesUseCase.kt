package com.movies.msearch.data.domain.usecases

import com.movies.msearch.data.network.ApiResponse
import com.movies.msearch.data.repository.MovieRepository
import com.movies.msearch.data.response.MovieResponse

class SearchMoviesUseCase(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(query: String): ApiResponse<MovieResponse> {
        return movieRepository.searchMovies(query)
    }
}