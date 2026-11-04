package com.enriquediaz.listacompra.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        val SUPERMARKET = stringPreferencesKey("supermarket")
        val SHOW_IMAGES = booleanPreferencesKey("show_images")
        val SHOW_PRICES = booleanPreferencesKey("show_prices")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    // Lee y actualiza las preferencias del usuario en tiempo real
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            supermarket = preferences[PreferencesKeys.SUPERMARKET] ?: "",
            showImages = preferences[PreferencesKeys.SHOW_IMAGES] ?: true,
            showPrices = preferences[PreferencesKeys.SHOW_PRICES] ?: true,
            hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
        )
    }

    suspend fun savePreferences(supermarket: String, showImages: Boolean, showPrices: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUPERMARKET] = supermarket
            preferences[PreferencesKeys.SHOW_IMAGES] = showImages
            preferences[PreferencesKeys.SHOW_PRICES] = showPrices
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = true
        }
    }
}
    data class UserPreferences(
        val supermarket: String = "",
        val showImages: Boolean = true,
        val showPrices: Boolean = true,
        val hasCompletedOnboarding: Boolean = false
    )