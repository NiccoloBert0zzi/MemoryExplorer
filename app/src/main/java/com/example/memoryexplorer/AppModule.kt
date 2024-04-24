package com.example.memoryexplorer

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.memoryexplorer.data.database.MemoryExplorerDatabase
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.screens.home.HomeViewModel
import com.example.memoryexplorer.ui.screens.login.LoginViewModel
import com.example.memoryexplorer.ui.screens.register.RegisterViewModel
import com.example.memoryexplorer.ui.screens.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// TODO perchè settings?
val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single {
        Room.databaseBuilder(
            get(),
            MemoryExplorerDatabase::class.java,
            "memory-explorer"
        ).fallbackToDestructiveMigration().build()
    }

    single { LoginRepository(get()) }
    single {
        FavouriteRepository(
            get<MemoryExplorerDatabase>().favouriteDAO()
        )
    }

    viewModel { HomeViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { SettingsViewModel() }

}
