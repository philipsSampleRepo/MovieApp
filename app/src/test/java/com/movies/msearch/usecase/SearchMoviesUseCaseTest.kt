package com.movies.msearch.data


import com.movies.msearch.data.domain.usecases.SearchMoviesUseCase
import com.movies.msearch.data.network.ApiResponse
import com.movies.msearch.data.repository.MovieRepository
import com.movies.msearch.data.response.MovieResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchMoviesUseCaseTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase

    @Before
    fun setup() {
        movieRepository = mockk()
        searchMoviesUseCase = SearchMoviesUseCase(movieRepository)
    }

    @Test
    fun `invoke with query returns Success when repository succeeds`() = runTest {
        val query = "Batman"
        val mockMovieResponse = MovieResponse(
            search = listOf(),
            totalResults = "1",
            response = "res"
        )
        val expectedResponse = ApiResponse.Success(mockMovieResponse)
        coEvery { movieRepository.searchMovies(query) } returns expectedResponse
        val result = searchMoviesUseCase(query)
        coVerify(exactly = 1) { movieRepository.searchMovies(query) }
        assertTrue(result is ApiResponse.Success)
        assertEquals(mockMovieResponse, (result as ApiResponse.Success).data)
    }

    @Test
    fun `invoke returns Error when repository fails`() = runTest {
        val query = "InvalidQuery"
        val errorMessage = "Network error"
        val expectedResponse = ApiResponse.Error(errorMessage)

        coEvery { movieRepository.searchMovies(query) } returns expectedResponse
        val result = searchMoviesUseCase(query)
        coVerify(exactly = 1) { movieRepository.searchMovies(query) }
        assertTrue(result is ApiResponse.Error)
        assertEquals(errorMessage, (result as ApiResponse.Error).message)
    }

    @Test
    fun `invoke with empty query calls repository with empty string`() = runTest {
        val query = ""
        val mockMovieResponse = MovieResponse(
            search = listOf(),
            totalResults = "1",
            response = "res"
        )
        val expectedResponse = ApiResponse.Success(mockMovieResponse)

        coEvery { movieRepository.searchMovies(query) } returns expectedResponse
        val result = searchMoviesUseCase(query)
        coVerify(exactly = 1) { movieRepository.searchMovies(query) }
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `invoke returns Loading when repository returns Loading`() = runTest {
        val query = "LoadingTest"
        coEvery { movieRepository.searchMovies(query) } returns ApiResponse.Loading
        val result = searchMoviesUseCase(query)
        coVerify(exactly = 1) { movieRepository.searchMovies(query) }
        assertTrue(result is ApiResponse.Loading)
    }
}