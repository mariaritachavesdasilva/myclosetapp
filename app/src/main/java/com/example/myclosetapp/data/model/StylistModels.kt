package com.example.myclosetapp.data.model

data class StylistRequest(
    val occasion: OccasionItem? = null,
    val style: StyleItem? = null,
    val weather: WeatherItem? = null
)

data class OutfitSuggestion(
    val items: List<ClothingItem>,
    val score: Int,
    val matchReasons: List<String>
)

data class UserPreferences(
    val name: String = "Ana",
    val email: String = "",
    val isLoggedIn: Boolean = true,
    val isVisitor: Boolean = false,
    val favoriteStyles: Set<StyleItem> = setOf(StyleItem.CASUAL, StyleItem.ELEGANTE, StyleItem.MINIMALISTA),
    val favoriteColors: Set<ColorItem> = setOf(ColorItem.BEGE, ColorItem.OFF_WHITE, ColorItem.MARROM, ColorItem.PRETO),
    val defaultWeather: WeatherItem = WeatherItem.AMENO,
    val avoidDaysThreshold: Int = 3 // 0 to 7 days
)

data class UserProfile(
    val name: String,
    val email: String,
    val isVisitor: Boolean
)
