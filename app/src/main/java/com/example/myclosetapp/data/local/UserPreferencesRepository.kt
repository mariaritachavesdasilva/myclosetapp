package com.example.myclosetapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.myclosetapp.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_VISITOR = booleanPreferencesKey("is_visitor")
        val FAVORITE_STYLES = stringSetPreferencesKey("favorite_styles")
        val FAVORITE_COLORS = stringSetPreferencesKey("favorite_colors")
        val DEFAULT_WEATHER = stringPreferencesKey("default_weather")
        val AVOID_DAYS_THRESHOLD = intPreferencesKey("avoid_days_threshold")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.USER_NAME] ?: "Ana"
        val email = preferences[PreferencesKeys.USER_EMAIL] ?: ""
        val isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        val isVisitor = preferences[PreferencesKeys.IS_VISITOR] ?: false
        
        val styleStrings = preferences[PreferencesKeys.FAVORITE_STYLES]
        val favStyles = styleStrings?.mapNotNull {
            try { StyleItem.valueOf(it) } catch (_: Exception) { null }
        }?.toSet() ?: setOf(StyleItem.CASUAL, StyleItem.ELEGANTE, StyleItem.MINIMALISTA)

        val colorStrings = preferences[PreferencesKeys.FAVORITE_COLORS]
        val favColors = colorStrings?.mapNotNull {
            try { ColorItem.valueOf(it) } catch (_: Exception) { null }
        }?.toSet() ?: setOf(ColorItem.BEGE, ColorItem.OFF_WHITE, ColorItem.MARROM, ColorItem.PRETO)

        val weatherStr = preferences[PreferencesKeys.DEFAULT_WEATHER]
        val defaultWeather = weatherStr?.let {
            try { WeatherItem.valueOf(it) } catch (_: Exception) { null }
        } ?: WeatherItem.AMENO

        val avoidDays = preferences[PreferencesKeys.AVOID_DAYS_THRESHOLD] ?: 3

        UserPreferences(
            name = name,
            email = email,
            isLoggedIn = isLoggedIn,
            isVisitor = isVisitor,
            favoriteStyles = favStyles,
            favoriteColors = favColors,
            defaultWeather = defaultWeather,
            avoidDaysThreshold = avoidDays
        )
    }

    suspend fun setUserProfile(name: String, email: String, isLoggedIn: Boolean, isVisitor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
            preferences[PreferencesKeys.IS_VISITOR] = isVisitor
        }
    }

    suspend fun updatePreferences(
        name: String,
        favoriteStyles: Set<StyleItem>,
        favoriteColors: Set<ColorItem>,
        defaultWeather: WeatherItem,
        avoidDaysThreshold: Int
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.FAVORITE_STYLES] = favoriteStyles.map { it.name }.toSet()
            preferences[PreferencesKeys.FAVORITE_COLORS] = favoriteColors.map { it.name }.toSet()
            preferences[PreferencesKeys.DEFAULT_WEATHER] = defaultWeather.name
            preferences[PreferencesKeys.AVOID_DAYS_THRESHOLD] = avoidDaysThreshold.coerceIn(0, 7)
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            preferences[PreferencesKeys.IS_VISITOR] = false
        }
    }
}
