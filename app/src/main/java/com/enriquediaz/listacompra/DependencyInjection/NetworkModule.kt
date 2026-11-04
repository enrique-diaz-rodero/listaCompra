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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 1. Retrofit para Open Food Facts (Imágenes y Búsqueda)
    @Provides
    @Singleton
    @Named("OffRetrofit")
    fun provideOffRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
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
        return Retrofit.Builder()
            .baseUrl("https://prices.openfoodfacts.org/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenPricesApi(@Named("PricesRetrofit") retrofit: Retrofit): OpenPricesApi {
        return retrofit.create(OpenPricesApi::class.java)
    }
}