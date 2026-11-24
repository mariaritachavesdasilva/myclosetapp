package com.example.myclosetapp.stylist

import com.example.myclosetapp.data.model.*

interface StylistEngine {
    fun suggest(
        request: StylistRequest,
        closet: List<ClothingItem>,
        prefs: UserPreferences,
        recentWornLogs: List<WearLog>,
        limit: Int = 5
    ): List<OutfitSuggestion>
}
