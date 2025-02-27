package com.movies.msearch.application

import android.app.Application
import com.movies.msearch.di.AppComponent
import com.movies.msearch.di.AppModule
import com.movies.msearch.di.DaggerAppComponent

class MoviesApp : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
    }
}