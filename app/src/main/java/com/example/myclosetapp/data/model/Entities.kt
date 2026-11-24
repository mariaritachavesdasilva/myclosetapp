package com.example.myclosetapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: Category,
    val color: ColorItem,
    val pattern: PatternItem,
    val styles: Set<StyleItem>,
    val occasions: Set<OccasionItem>,
    val seasons: Set<SeasonItem>,
    val brand: String = "",
    val notes: String = "",
    val imagePath: String? = null,
    val isFavorite: Boolean = false,
    val timesWorn: Int = 0,
    val lastWornTimestamp: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "outfits")
data class Outfit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val occasion: OccasionItem? = null,
    val style: StyleItem? = null,
    val weather: WeatherItem? = null,
    val itemIds: List<Long>,
    val isFavorite: Boolean = false,
    val timesWorn: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wear_logs")
data class WearLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val outfitId: Long? = null,
    val title: String,
    val itemIds: List<Long>,
    val occasion: OccasionItem? = null,
    val wornDate: Long = System.currentTimeMillis()
)
