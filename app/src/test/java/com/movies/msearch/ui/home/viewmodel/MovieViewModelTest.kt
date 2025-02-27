package com.movies.msearch.ui.home.viewmodel

import com.movies.msearch.data.domain.usecases.SearchMoviesUseCase
import com.movies.msearch.data.network.ApiResponse
import com.movies.msearch.data.network.NoInternetException
import com.movies.msearch.data.response.MovieResponse
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieViewModelTest {

    private lateinit var viewModel: MovieViewModel
    private val searchMoviesUseCase: SearchMoviesUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieViewModel(searchMoviesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchMovies emits Loading followed by Success`() = runTest {
        val mockResponse = MovieResponse(
            search = listOf(),
            totalResults = "1",
            response = "res"
        )
        coEvery { searchMoviesUseCase("Inception") } returns ApiResponse.Success(mockResponse)
        val emissions = mutableListOf<ApiResponse<MovieResponse>>()
        val job = launch { viewModel.moviesState.toList(emissions) }

        viewModel.searchMovies("Inception")
        advanceUntilIdle()

        assertTrue(emissions[0] is ApiResponse.Loading)
        assertTrue(emissions[1] is ApiResponse.Success)
        assertEquals(mockResponse, (emissions[1] as ApiResponse.Success).data)

        job.cancel()
    }

    @Test
    fun `searchMovies emits Loading followed by Error on Exception`() = runTest {
        coEvery { searchMoviesUseCase("InvalidMovie") } throws RuntimeException("Unknown error")
        val emissions = mutableListOf<ApiResponse<MovieResponse>>()
        val job = launch { viewModel.moviesState.toList(emissions) }

        viewModel.searchMovies("InvalidMovie")
        advanceUntilIdle()

        assertTrue(emissions[0] is ApiResponse.Loading)
        assertTrue(emissions[1] is ApiResponse.Error)
        assertEquals(
            "No internet connection. Please try again later.",
            (emissions[1] as ApiResponse.Error).message
        )

        job.cancel()
    }

    @Test
    fun `searchMovies emits Loading followed by NoInternetException error`() = runTest {
        coEvery { searchMoviesUseCase("NoInternet") } throws NoInternetException("exception")

        val emissions = mutableListOf<ApiResponse<MovieResponse>>()
        val job = launch { viewModel.moviesState.toList(emissions) }

        viewModel.searchMovies("NoInternet")
        advanceUntilIdle()

        assertTrue(emissions[0] is ApiResponse.Loading)
        assertTrue(emissions[1] is ApiResponse.Error)
        assertEquals(
            "No internet connection. Please try again later.",
            (emissions[1] as ApiResponse.Error).message
        )

        job.cancel()
    }
}
