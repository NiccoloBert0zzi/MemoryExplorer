package com.example.memoryexplorer.data.repositories

import com.example.memoryexplorer.data.database.Favourite
import com.example.memoryexplorer.data.database.FavouriteDAO
import kotlinx.coroutines.flow.Flow

class FavouriteRepository(
    private val favouriteDao: FavouriteDAO,
) {
    val favourites: Flow<List<Favourite>> = favouriteDao.getAll()

    suspend fun upsert(favourite: Favourite) {
        favouriteDao.upsert(favourite)
    }

    suspend fun delete(favourite: Favourite) {
        favouriteDao.delete(favourite)
    }
}