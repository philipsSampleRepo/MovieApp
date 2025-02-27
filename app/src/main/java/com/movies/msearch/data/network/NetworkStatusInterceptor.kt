package com.movies.msearch.data.network

import android.content.Context
import com.movies.msearch.utils.NetworkUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkStatusInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkUtils.isInternetAvailable(context)) {
            throw NoInternetException("No internet connection") // Custom exception
        }
        return chain.proceed(chain.request())
    }
}

class NoInternetException(message: String) : IOException(message)