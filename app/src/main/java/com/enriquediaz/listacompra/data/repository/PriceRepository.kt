package com.enriquediaz.listacompra.data.repository

import android.util.Log
import com.enriquediaz.listacompra.data.remote.OpenPricesApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepository @Inject constructor(
    private val api: OpenPricesApi
) {
    suspend fun getPriceForProduct(productCode: String, preferredStore: String): Result<Double?> {
        return try {
            val response = api.getPricesByProductCode(productCode)

            // Filtramos por la tienda preferida del usuario (ignorando mayúsculas/minúsculas)
            val storePrice = response.items.firstOrNull {
                it.storeName?.contains(preferredStore, ignoreCase = true) == true
            }

            // Si no hay precio en su tienda, cogemos el último precio reportado (o null)
            val finalPrice = storePrice?.price ?: response.items.lastOrNull()?.price

            Result.success(finalPrice)
        } catch (e: Exception) {
            Log.e("PriceDebug", "Error obteniendo precio: ${e.message}")
            Result.failure(e)
        }
    }
}