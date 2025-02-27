package com.movies.msearch.data.repository

import com.movies.msearch.data.network.ApiResponse
import com.movies.msearch.data.network.MovieApiService
import com.movies.msearch.data.response.MovieResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MovieRepositoryTest {

    private lateinit var movieApiService: MovieApiService
    private lateinit var movieRepository: MovieRepository

    @Before
    fun setUp() {
        movieApiService = mock(MovieApiService::class.java)
        movieRepository = MovieRepository(movieApiService)
    }

    @Test
    fun `searchMovies returns Success when API call is successful`() = runBlocking {
        // Mock response
        val mockResponse = MovieResponse(
            search = listOf(),
            totalResults = "1",
            response = "res"
        )
        whenever(movieApiService.searchMovies(anyString(), anyString())).thenReturn(mockResponse)
        val result = movieRepository.searchMovies("Inception")
        assertTrue(result is ApiResponse.Success)
        assertEquals(mockResponse, (result as ApiResponse.Success).data)
    }

    @Test
    fun `searchMovies returns Error when API call fails`() = runBlocking {
        whenever(movieApiService.searchMovies(anyString(), anyString())).thenThrow(
            RuntimeException(
                "Network Error"
            )
        )
        val result = movieRepository.searchMovies("InvalidMovie")
        assertTrue(result is ApiResponse.Error)
        assertEquals("Network Error", (result as ApiResponse.Error).message)
    }
}
