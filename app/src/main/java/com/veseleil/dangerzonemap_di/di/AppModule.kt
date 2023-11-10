package com.veseleil.dangerzonemap_di.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.data.remote.ApiService
import com.veseleil.dangerzonemap_di.data.repository.UserRepository
import com.veseleil.dangerzonemap_di.utils.Constants
import com.veseleil.dangerzonemap_di.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApi(): ApiService {

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: ApiService): UserRepository {
        return UserRepository(api)
    }

    @Provides
    @Singleton
    fun provideSessionManager(prefs: SharedPreferences): SessionManager {
        return SessionManager(prefs)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(appContext: Application): SharedPreferences {
        return appContext.getSharedPreferences(appContext.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

}