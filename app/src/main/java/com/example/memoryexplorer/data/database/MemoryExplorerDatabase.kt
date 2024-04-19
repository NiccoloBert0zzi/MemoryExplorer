package com.example.memoryexplorer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Favourite::class], version = 1)
abstract class MemoryExplorerDatabase : RoomDatabase() {
    abstract fun favouriteDAO(): FavouriteDAO
}
