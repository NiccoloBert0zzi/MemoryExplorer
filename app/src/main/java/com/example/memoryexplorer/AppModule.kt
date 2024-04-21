package com.example.memoryexplorer

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.memoryexplorer.data.database.MemoryExplorerDatabase
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.screens.login.LoginScreen
import com.example.memoryexplorer.ui.screens.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single {
        Room.databaseBuilder(
            get(),
            MemoryExplorerDatabase::class.java,
            "memory-explorer"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { LoginRepository(get()) }

    viewModel{ LoginViewModel(get()) }

}