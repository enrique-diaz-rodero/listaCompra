package com.enriquediaz.listacompra.di

import com.enriquediaz.listacompra.data.remote.OpenFoodFactsApi
import com.enriquediaz.listacompra.data.remote.OpenPricesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import javax.inject.Named
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.Firebase
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 1. Retrofit para Open Food Facts (Imágenes y Búsqueda)
    @Provides
    @Singleton
    @Named("OffRetrofit")
    fun provideOffRetrofit(): Retrofit {
        // CREAMOS EL CLIENTE CON TIMEOUTS
        val client = OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS) // 5 seg para conectar
            .readTimeout(15, TimeUnit.SECONDS)   // 15 seg para leer la respuesta
            .build()

        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(client) // <-- AÑADIMOS EL CLIENTE AQUÍ
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenFoodFactsApi(@Named("OffRetrofit") retrofit: Retrofit): OpenFoodFactsApi {
        return retrofit.create(OpenFoodFactsApi::class.java)
    }

    // 2. Retrofit para Open Prices (Precios)
    @Provides
    @Singleton
    @Named("PricesRetrofit")
    fun providePricesRetrofit(): Retrofit {
        // TAMBIÉN PODEMOS APLICAR TIMEOUTS A LOS PRECIOS
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://prices.openfoodfacts.org/api/v1/")
            .client(client) // <-- AÑADIDO
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenPricesApi(@Named("PricesRetrofit") retrofit: Retrofit): OpenPricesApi {
        return retrofit.create(OpenPricesApi::class.java)
    }
    @Provides
    @Singleton
    fun provideAnalytics(): FirebaseAnalytics = Firebase.analytics
}