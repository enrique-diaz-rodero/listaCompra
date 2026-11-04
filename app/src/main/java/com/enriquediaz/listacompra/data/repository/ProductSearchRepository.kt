package com.enriquediaz.listacompra.data.repository

import android.util.Log
import com.enriquediaz.listacompra.data.remote.OffProduct
import com.enriquediaz.listacompra.data.remote.OpenFoodFactsApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductSearchRepository @Inject constructor(
    private val api: OpenFoodFactsApi
) {
    suspend fun searchProducts(query: String, preferredStore: String?): Result<List<OffProduct>> {
        return try {
            if (query.length < 3) return Result.success(emptyList()) // Evitar buscar con 1 letra

            // 1. Intento con filtro de tienda
            if (!preferredStore.isNullOrBlank()) {
                val filtered = api.searchProducts(query = query, store = preferredStore)
                if (filtered.products.isNotEmpty()) {
                    Log.d("SearchDebug", "Encontrados ${filtered.products.size} con filtro de tienda")
                    return Result.success(filtered.products)
                }
            }

            // 2. Respaldo (Fallback) sin filtro de tienda
            Log.d("SearchDebug", "Buscando sin filtro de tienda (Fallback)")
            val general = api.searchProducts(query = query, store = null)
            Result.success(general.products)

        } catch (e: Exception) {
            Log.e("SearchDebug", "Error de red: ${e.message}")
            Result.failure(e)
        }
    }
}