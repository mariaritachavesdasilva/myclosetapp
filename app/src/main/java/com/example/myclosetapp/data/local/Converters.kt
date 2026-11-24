package com.example.myclosetapp.data.local

import androidx.room.TypeConverter
import com.example.myclosetapp.data.model.*

class Converters {
    @TypeConverter
    fun fromCategory(value: Category?): String? = value?.name

    @TypeConverter
    fun toCategory(value: String?): Category? = value?.let {
        try { Category.valueOf(it) } catch (_: Exception) { null }
    }

    @TypeConverter
    fun fromColorItem(value: ColorItem?): String? = value?.name

    @TypeConverter
    fun toColorItem(value: String?): ColorItem? = value?.let {
        try { ColorItem.valueOf(it) } catch (_: Exception) { null }
    }

    @TypeConverter
    fun fromPatternItem(value: PatternItem?): String? = value?.name

    @TypeConverter
    fun toPatternItem(value: String?): PatternItem? = value?.let {
        try { PatternItem.valueOf(it) } catch (_: Exception) { null }
    }

    @TypeConverter
    fun fromStyleItem(value: StyleItem?): String? = value?.name

    @TypeConverter
    fun toStyleItem(value: String?): StyleItem? = value?.let {
        try { StyleItem.valueOf(it) } catch (_: Exception) { null }
    }

    @TypeConverter
    fun fromOccasionItem(value: OccasionItem?): String? = value?.name

    @TypeConverter
    fun toOccasionItem(value: String?): OccasionItem? = value?.let {
        try { OccasionItem.valueOf(it) } catch (_: Exception) { null }
    }

    @TypeConverter
    fun fromWeatherItem(value: WeatherItem?): String? = value?.name

    @TypeConverter
    fun toWeatherItem(value: String?): WeatherItem? = value?.let {
        try { WeatherItem.valueOf(it) } catch (_: Exception) { null }
    }

    @TypeConverter
    fun fromStyleSet(set: Set<StyleItem>?): String =
        set?.joinToString(",") { it.name } ?: ""

    @TypeConverter
    fun toStyleSet(value: String?): Set<StyleItem> =
        if (value.isNullOrBlank()) emptySet()
        else value.split(",").mapNotNull {
            try { StyleItem.valueOf(it.trim()) } catch (_: Exception) { null }
        }.toSet()

    @TypeConverter
    fun fromOccasionSet(set: Set<OccasionItem>?): String =
        set?.joinToString(",") { it.name } ?: ""

    @TypeConverter
    fun toOccasionSet(value: String?): Set<OccasionItem> =
        if (value.isNullOrBlank()) emptySet()
        else value.split(",").mapNotNull {
            try { OccasionItem.valueOf(it.trim()) } catch (_: Exception) { null }
        }.toSet()

    @TypeConverter
    fun fromSeasonSet(set: Set<SeasonItem>?): String =
        set?.joinToString(",") { it.name } ?: ""

    @TypeConverter
    fun toSeasonSet(value: String?): Set<SeasonItem> =
        if (value.isNullOrBlank()) emptySet()
        else value.split(",").mapNotNull {
            try { SeasonItem.valueOf(it.trim()) } catch (_: Exception) { null }
        }.toSet()

    @TypeConverter
    fun fromLongList(list: List<Long>?): String =
        list?.joinToString(",") ?: ""

    @TypeConverter
    fun toLongList(value: String?): List<Long> =
        if (value.isNullOrBlank()) emptyList()
        else value.split(",").mapNotNull { it.trim().toLongOrNull() }
}
