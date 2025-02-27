package com.movies.msearch.data.repository

import com.movies.msearch.BuildConfig
import com.movies.msearch.data.network.ApiResponse
import com.movies.msearch.data.network.MovieApiService
import com.movies.msearch.data.response.MovieResponse

class MovieRepository(private val movieApiService: MovieApiService) {
    suspend fun searchMovies(query: String): ApiResponse<MovieResponse> {
        return try {
            val response = movieApiService.searchMovies(BuildConfig.API_KEY, query)
            ApiResponse.Success(response)
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "An error occurred")
        }
    }
}