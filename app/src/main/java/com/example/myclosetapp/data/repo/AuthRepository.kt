package com.example.myclosetapp.data.repo

import com.example.myclosetapp.data.local.UserPreferencesRepository
import com.example.myclosetapp.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AuthRepository {
    val userPreferences: Flow<UserPreferences>
    val isLoggedIn: Flow<Boolean>

    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signup(name: String, email: String, password: String): Result<Unit>
    suspend fun continueAsVisitor(): Result<Unit>
    suspend fun logout()
    suspend fun updatePreferences(
        name: String,
        favoriteStyles: Set<StyleItem>,
        favoriteColors: Set<ColorItem>,
        defaultWeather: WeatherItem,
        avoidDaysThreshold: Int
    )
}

class LocalAuthRepository(
    private val preferencesRepository: UserPreferencesRepository
) : AuthRepository {

    override val userPreferences: Flow<UserPreferences> = preferencesRepository.userPreferencesFlow

    override val isLoggedIn: Flow<Boolean> = preferencesRepository.userPreferencesFlow.map { 
        it.isLoggedIn || it.isVisitor 
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || !isValidEmail(trimmedEmail)) {
            return Result.failure(IllegalArgumentException("Por favor, informe um e-mail válido."))
        }
        if (trimmedPassword.length < 6) {
            return Result.failure(IllegalArgumentException("A senha deve ter pelo menos 6 caracteres."))
        }

        // Simula login local bem-sucedido
        val nameFromEmail = trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
        preferencesRepository.setUserProfile(
            name = if (nameFromEmail.isNotBlank()) nameFromEmail else "Ana",
            email = trimmedEmail,
            isLoggedIn = true,
            isVisitor = false
        )
        return Result.success(Unit)
    }

    override suspend fun signup(name: String, email: String, password: String): Result<Unit> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Por favor, informe seu nome."))
        }
        if (trimmedEmail.isBlank() || !isValidEmail(trimmedEmail)) {
            return Result.failure(IllegalArgumentException("Por favor, informe um e-mail válido."))
        }
        if (trimmedPassword.length < 6) {
            return Result.failure(IllegalArgumentException("A senha deve ter pelo menos 6 caracteres."))
        }

        preferencesRepository.setUserProfile(
            name = trimmedName,
            email = trimmedEmail,
            isLoggedIn = true,
            isVisitor = false
        )
        return Result.success(Unit)
    }

    override suspend fun continueAsVisitor(): Result<Unit> {
        preferencesRepository.setUserProfile(
            name = "Visitante",
            email = "",
            isLoggedIn = true,
            isVisitor = true
        )
        return Result.success(Unit)
    }

    override suspend fun logout() {
        preferencesRepository.logout()
    }

    override suspend fun updatePreferences(
        name: String,
        favoriteStyles: Set<StyleItem>,
        favoriteColors: Set<ColorItem>,
        defaultWeather: WeatherItem,
        avoidDaysThreshold: Int
    ) {
        preferencesRepository.updatePreferences(
            name = name,
            favoriteStyles = favoriteStyles,
            favoriteColors = favoriteColors,
            defaultWeather = defaultWeather,
            avoidDaysThreshold = avoidDaysThreshold
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
