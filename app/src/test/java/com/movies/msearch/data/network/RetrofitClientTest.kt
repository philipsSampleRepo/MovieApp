package com.movies.msearch.data.network

import android.content.Context
import com.movies.msearch.BuildConfig
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientTest {
    private lateinit var context: Context
    private lateinit var movieApiService: MovieApiService

    @Before
    fun setUp() {
        // Mock context for interceptor
        context = Mockito.mock(Context::class.java)

        // Create API service
        movieApiService = RetrofitClient.create(context)
    }

    @Test
    fun `Retrofit instance should have correct base URL`() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        assertEquals(BuildConfig.BASE_URL, retrofit.baseUrl().toString())
    }

    @Test
    fun `OkHttpClient should contain logging interceptor`() {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val hasLoggingInterceptor = okHttpClient.interceptors.any { it is HttpLoggingInterceptor }
        assertTrue(hasLoggingInterceptor)
    }

    @Test
    fun `Retrofit should return a valid MovieApiService instance`() {
        assertNotNull(movieApiService)
    }
}