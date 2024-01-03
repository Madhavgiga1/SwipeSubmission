package com.example.swipecode.di


import com.example.swipecode.Utils.Constants.Companion.BASE_URL
import com.example.swipecode.data.SwipeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideHttpClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun providesRetrofitInstance(
        okHttpClient: OkHttpClient,
        gsonConverterFactory:GsonConverterFactory
    ):Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory).build()
    }
    /*@Provides annotation, which indicates that it is used to provide a dependency for the app. The method is also annotated
    with the @Singleton annotation, which indicates that the dependency is a singleton instance. This means that the same
    instance of the SwipeApi interface will be provided to all components of the app that depend on it.*/
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): SwipeApi {
        return retrofit.create(SwipeApi::class.java)
    }

}

