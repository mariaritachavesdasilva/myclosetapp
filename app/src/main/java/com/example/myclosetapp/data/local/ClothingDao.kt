package com.example.myclosetapp.data.local

import androidx.room.*
import com.example.myclosetapp.data.model.ClothingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {
    @Query("SELECT * FROM clothing_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteItems(): Flow<List<ClothingItem>>

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    suspend fun getItemById(id: Long): ClothingItem?

    @Query("SELECT * FROM clothing_items WHERE id IN (:ids)")
    suspend fun getItemsByIds(ids: List<Long>): List<ClothingItem>

    @Query("SELECT COUNT(*) FROM clothing_items")
    fun getItemsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM clothing_items WHERE isFavorite = 1")
    fun getFavoriteItemsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ClothingItem): Long

    @Update
    suspend fun updateItem(item: ClothingItem)

    @Delete
    suspend fun deleteItem(item: ClothingItem)

    @Query("DELETE FROM clothing_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)
}
