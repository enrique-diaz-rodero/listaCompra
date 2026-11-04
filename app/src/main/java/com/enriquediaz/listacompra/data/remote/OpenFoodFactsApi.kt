package com.enriquediaz.listacompra.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenFoodFactsApi {
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("stores") store: String? = null, // Opcional
        @Query("search_simple") searchSimple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 5
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