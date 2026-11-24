package com.example.myclosetapp.data.local

import androidx.room.*
import com.example.myclosetapp.data.model.WearLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WearLogDao {
    @Query("SELECT * FROM wear_logs ORDER BY wornDate DESC")
    fun getAllWearLogs(): Flow<List<WearLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWearLog(log: WearLog): Long

    @Delete
    suspend fun deleteWearLog(log: WearLog)

    @Query("DELETE FROM wear_logs WHERE id = :id")
    suspend fun deleteWearLogById(id: Long)

    @Query("DELETE FROM wear_logs")
    suspend fun clearAll()
}
