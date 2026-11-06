package com.enriquediaz.listacompra.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenFoodFactsApi {
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("stores") store: String? = null, // Opcional
        @Query("countries_tags") country: String, // <-- Filtro por país
        @Query("lc") language: String,                 // <-- Filtro por idioma
        @Query("search_simple") searchSimple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 30, // <-- Aumentar el tamaño de la página

        // NUEVO: Filtramos para que solo devuelva estos 3 campos
        @Query("fields") fields: String = "code,product_name,image_front_small_url"
    ): OffSearchResponse
}

data class OffSearchResponse(
    @SerializedName("products") val products: List<OffProduct>
)

data class OffProduct(
    @SerializedName("code") val code: String?, // El EAN
    @SerializedName("product_name") val name: String?,
    @SerializedName("image_front_small_url") val imageUrl: String?
)