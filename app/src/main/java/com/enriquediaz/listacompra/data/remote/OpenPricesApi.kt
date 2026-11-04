package com.enriquediaz.listacompra.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenPricesApi {
    // Buscamos precios por el código de barras (EAN)
    @GET("prices")
    suspend fun getPricesByProductCode(
        @Query("product_code") productCode: String
    ): OpenPricesResponse
}

// Modelos de respuesta
data class OpenPricesResponse(
    @SerializedName("items") val items: List<PriceItem>
)

data class PriceItem(
    @SerializedName("price") val price: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("location_osm_name") val storeName: String? // Nombre de la tienda
)