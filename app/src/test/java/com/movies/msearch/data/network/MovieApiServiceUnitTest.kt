package com.movies.msearch.data.network


import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MovieApiServiceUnitTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var movieApiService: MovieApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Use MockWebServer's URL
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        movieApiService = retrofit.create(MovieApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `searchMovies returns successful response`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"Response": "1", "Search": [], "totalResults": "1"}""") // Sample MovieResponse JSON
        mockWebServer.enqueue(mockResponse)

        val response = movieApiService.searchMovies("test_api_key", "query")

        assert(response.response.equals("1"))
        response.search?.let { assert(it?.isEmpty() == true) }
        val request = mockWebServer.takeRequest()
        assert(request.path == "/?apikey=test_api_key&s=query")
    }

    @Test
    fun `searchMovies handles HTTP error`() = runTest {
        val mockResponse = MockResponse().setResponseCode(404)
        mockWebServer.enqueue(mockResponse)

        try {
            movieApiService.searchMovies("invalid_key", "query")
            assert(false)
        } catch (e: Exception) {
            assert(e is retrofit2.HttpException)
            assert((e as retrofit2.HttpException).code() == 404)
        }
    }

    @Test
    fun `searchMovies handles invalid JSON`() = runTest {
        val mockResponse = MockResponse()
            .setBody("{invalid_json}")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        try {
            movieApiService.searchMovies("api_key", "query")
            assert(false)
        } catch (e: Exception) {
            assert(e is com.google.gson.stream.MalformedJsonException)
        }
    }

    @Test
    fun `searchMovies handles empty query`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("""{"page": 1, "results": []}"""))
        val response = movieApiService.searchMovies("api_key", "")
        assert( response.let {
            it.search?.isNotEmpty() ?: true
        })
    }
}
