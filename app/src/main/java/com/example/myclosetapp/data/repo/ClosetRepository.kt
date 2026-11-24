package com.example.myclosetapp.data.repo

import com.example.myclosetapp.data.local.ClothingDao
import com.example.myclosetapp.data.local.OutfitDao
import com.example.myclosetapp.data.local.WearLogDao
import com.example.myclosetapp.data.model.*
import kotlinx.coroutines.flow.Flow

interface ClosetRepository {
    // Items
    val allItems: Flow<List<ClothingItem>>
    val favoriteItems: Flow<List<ClothingItem>>
    val itemsCount: Flow<Int>
    val favoriteItemsCount: Flow<Int>

    suspend fun getItemById(id: Long): ClothingItem?
    suspend fun getItemsByIds(ids: List<Long>): List<ClothingItem>
    suspend fun saveItem(item: ClothingItem): Long
    suspend fun deleteItem(item: ClothingItem)

    // Outfits
    val allOutfits: Flow<List<Outfit>>
    val favoriteOutfits: Flow<List<Outfit>>
    val outfitsCount: Flow<Int>

    suspend fun getOutfitById(id: Long): Outfit?
    suspend fun saveOutfit(outfit: Outfit): Long
    suspend fun toggleOutfitFavorite(outfit: Outfit)
    suspend fun deleteOutfit(outfit: Outfit)

    // Wear Logs (History)
    val allWearLogs: Flow<List<WearLog>>

    suspend fun logOutfitWorn(
        outfitId: Long?,
        title: String,
        itemIds: List<Long>,
        occasion: OccasionItem?
    )

    suspend fun deleteWearLog(log: WearLog)
    suspend fun clearHistory()
}

class LocalClosetRepository(
    private val clothingDao: ClothingDao,
    private val outfitDao: OutfitDao,
    private val wearLogDao: WearLogDao
) : ClosetRepository {

    override val allItems: Flow<List<ClothingItem>> = clothingDao.getAllItems()
    override val favoriteItems: Flow<List<ClothingItem>> = clothingDao.getFavoriteItems()
    override val itemsCount: Flow<Int> = clothingDao.getItemsCount()
    override val favoriteItemsCount: Flow<Int> = clothingDao.getFavoriteItemsCount()

    override suspend fun getItemById(id: Long): ClothingItem? = clothingDao.getItemById(id)
    override suspend fun getItemsByIds(ids: List<Long>): List<ClothingItem> = clothingDao.getItemsByIds(ids)

    override suspend fun saveItem(item: ClothingItem): Long {
        return if (item.id == 0L) {
            clothingDao.insertItem(item)
        } else {
            clothingDao.updateItem(item)
            item.id
        }
    }

    override suspend fun deleteItem(item: ClothingItem) {
        clothingDao.deleteItem(item)
    }

    override val allOutfits: Flow<List<Outfit>> = outfitDao.getAllOutfits()
    override val favoriteOutfits: Flow<List<Outfit>> = outfitDao.getFavoriteOutfits()
    override val outfitsCount: Flow<Int> = outfitDao.getOutfitsCount()

    override suspend fun getOutfitById(id: Long): Outfit? = outfitDao.getOutfitById(id)

    override suspend fun saveOutfit(outfit: Outfit): Long {
        return if (outfit.id == 0L) {
            outfitDao.insertOutfit(outfit)
        } else {
            outfitDao.updateOutfit(outfit)
            outfit.id
        }
    }

    override suspend fun toggleOutfitFavorite(outfit: Outfit) {
        outfitDao.updateOutfit(outfit.copy(isFavorite = !outfit.isFavorite))
    }

    override suspend fun deleteOutfit(outfit: Outfit) {
        outfitDao.deleteOutfit(outfit)
    }

    override val allWearLogs: Flow<List<WearLog>> = wearLogDao.getAllWearLogs()

    override suspend fun logOutfitWorn(
        outfitId: Long?,
        title: String,
        itemIds: List<Long>,
        occasion: OccasionItem?
    ) {
        val now = System.currentTimeMillis()
        val log = WearLog(
            outfitId = outfitId,
            title = title,
            itemIds = itemIds,
            occasion = occasion,
            wornDate = now
        )
        wearLogDao.insertWearLog(log)

        // Increment times worn for outfit if exists
        if (outfitId != null) {
            val outfit = outfitDao.getOutfitById(outfitId)
            if (outfit != null) {
                outfitDao.updateOutfit(outfit.copy(timesWorn = outfit.timesWorn + 1))
            }
        }

        // Increment times worn and update lastWornTimestamp for each item
        val items = clothingDao.getItemsByIds(itemIds)
        items.forEach { item ->
            clothingDao.updateItem(
                item.copy(
                    timesWorn = item.timesWorn + 1,
                    lastWornTimestamp = now
                )
            )
        }
    }

    override suspend fun deleteWearLog(log: WearLog) {
        wearLogDao.deleteWearLog(log)
    }

    override suspend fun clearHistory() {
        wearLogDao.clearAll()
    }
}
