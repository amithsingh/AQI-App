package com.example.aqidemoapp.utils

import com.example.aqidemoapp.remote.AQIApiService
import com.example.aqidemoapp.remote.AQIServiceQualifier
import com.example.aqidemoapp.remote.GeocodingApiService
import com.example.aqidemoapp.remote.GeocodingServiceQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @AQIServiceQualifier
    fun provideAQIRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.waqi.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @GeocodingServiceQualifier
    fun provideGeocodingRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.opencagedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAQIApiService(@AQIServiceQualifier retrofit: Retrofit): AQIApiService {
        return retrofit.create(AQIApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingApiService(@GeocodingServiceQualifier retrofit: Retrofit): GeocodingApiService {
        return retrofit.create(GeocodingApiService::class.java)
    }
}
