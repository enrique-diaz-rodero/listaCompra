package com.enriquediaz.listacompra.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {
    private object PreferencesKeys {
        val SUPERMARKET = stringPreferencesKey("supermarket")
        val SHOW_IMAGES = booleanPreferencesKey("show_images")
        val SHOW_PRICES = booleanPreferencesKey("show_prices")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    // Lee y actualiza las preferencias del usuario en tiempo real
    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            supermarket = prefs[PreferencesKeys.SUPERMARKET] ?: "",
            showImages = prefs[PreferencesKeys.SHOW_IMAGES] ?: true,
            showPrices = prefs[PreferencesKeys.SHOW_PRICES] ?: false,
            hasCompletedOnboarding = prefs[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
        )
    }

    suspend fun savePreferences(supermarket: String, showImages: Boolean, showPrices: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SUPERMARKET] = supermarket
            prefs[PreferencesKeys.SHOW_IMAGES] = showImages
            prefs[PreferencesKeys.SHOW_PRICES] = showPrices
            prefs[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = true
        }
    }
}

data class UserPreferences(
    val supermarket: String = "",
    val showImages: Boolean = true,
    val showPrices: Boolean = false,
    val hasCompletedOnboarding: Boolean = false
)