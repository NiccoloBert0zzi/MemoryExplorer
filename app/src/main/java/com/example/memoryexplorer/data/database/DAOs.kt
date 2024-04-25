package com.example.memoryexplorer.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDAO {
    @Query("SELECT * FROM favourite")
    fun getAll(): Flow<List<Favourite>>

    @Upsert
    suspend fun upsert(favourite: Favourite)

    @Delete
    suspend fun delete(favourite: Favourite)

    @Query("DELETE FROM favourite")
    suspend fun deleteAll()
}
