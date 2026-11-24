package com.example.myclosetapp.di

import android.content.Context
import com.example.myclosetapp.data.local.AppDatabase
import com.example.myclosetapp.data.local.UserPreferencesRepository
import com.example.myclosetapp.data.repo.AuthRepository
import com.example.myclosetapp.data.repo.ClosetRepository
import com.example.myclosetapp.data.repo.LocalAuthRepository
import com.example.myclosetapp.data.repo.LocalClosetRepository
import com.example.myclosetapp.stylist.LocalRulesStylist
import com.example.myclosetapp.stylist.StylistEngine

interface AppContainer {
    val database: AppDatabase
    val authRepository: AuthRepository
    val closetRepository: ClosetRepository
    val userPreferencesRepository: UserPreferencesRepository
    val stylistEngine: StylistEngine
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    override val authRepository: AuthRepository by lazy {
        LocalAuthRepository(userPreferencesRepository)
    }

    override val closetRepository: ClosetRepository by lazy {
        LocalClosetRepository(
            clothingDao = database.clothingDao(),
            outfitDao = database.outfitDao(),
            wearLogDao = database.wearLogDao()
        )
    }

    override val stylistEngine: StylistEngine by lazy {
        LocalRulesStylist()
    }
}
