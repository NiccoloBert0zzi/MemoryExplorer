package com.example.memoryexplorer.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDAO {
    @Query("SELECT * FROM Favourite")
    fun getAll(): Flow<List<Favourite>>

    @Query("SELECT * FROM Favourite WHERE email = :email")
    fun getUserMemories(email: String?): Flow<List<Favourite>>?

    @Query("SELECT * FROM Favourite WHERE email = :email AND memoryId = :memoryId")
    fun checkMemories(email: String?, memoryId: String?): Favourite?

    @Upsert
    suspend fun upsert(favourite: Favourite)

    @Delete
    suspend fun delete(favourite: Favourite)
}
