package com.enriquediaz.listacompra.data.repository

import android.util.Log
import com.enriquediaz.listacompra.data.remote.OffProduct
import com.enriquediaz.listacompra.data.remote.OpenFoodFactsApi
import java.util.Locale // <-- IMPORTANTE: Importar Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductSearchRepository @Inject constructor(
    private val api: OpenFoodFactsApi
) {
    // NUEVO: La "libreta" de caché en memoria para guardar las búsquedas.
    // Evita llamar a la API repetidamente y consume menos datos/límite.
    private val searchCache = HashMap<String, List<OffProduct>>()
    suspend fun searchProducts(query: String, preferredStore: String?): Result<List<OffProduct>> {
        return try {
            if (query.length < 3) return Result.success(emptyList()) // Evitar buscar con 1 letra

            // 1. NUEVO: Comprobamos si ya hemos buscado esto antes (ignorando mayúsculas/minúsculas)
            val cacheKey = query.lowercase().trim()
            if (searchCache.containsKey(cacheKey)) {
                Log.d("SearchDebug", "¡Resultados obtenidos de la CACHÉ LOCAL para: '$query'!")
                return Result.success(searchCache[cacheKey] ?: emptyList())
            }

            // 1.Detectar país e idioma del dispositivo
            val locale = Locale.getDefault()
            val languageCode = locale.language
            val countryCode = if (locale.country.isNotEmpty()) {
                "en:${locale.country.lowercase()}"
            } else {
                "en:world"
            }

            // 2. Intento con filtro de tienda
            if (!preferredStore.isNullOrBlank()) {
                val filtered = api.searchProducts(
                    query = query,
                    store = preferredStore,
                    country = countryCode,
                    language = languageCode
                )
                if (filtered.products.isNotEmpty()) {
                    Log.d("SearchDebug", "Encontrados ${filtered.products.size} con filtro de tienda")

                    // NUEVO: Guardamos en la caché antes de devolver
                    searchCache[cacheKey] = filtered.products
                    return Result.success(filtered.products)
                }
            }

            // 3. Respaldo (Fallback) sin filtro de tienda
            Log.d("SearchDebug", "Buscando sin filtro de tienda (Fallback)")
            val general = api.searchProducts(
                query = query,
                store = null,
                country = countryCode,
                language = languageCode
            )
            // NUEVO: Guardamos el resultado general en la caché antes de devolver
            searchCache[cacheKey] = general.products
            Result.success(general.products)

        } catch (e: Exception) {
            Log.e("SearchDebug", "Error de red: ${e.message}")
            Result.failure(e)
        }
    }
}