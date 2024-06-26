package com.example.memoryexplorer

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.memoryexplorer.data.database.MemoryExplorerDatabase
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.data.repositories.ThemeRepository
import com.example.memoryexplorer.ui.screens.addmemory.AddMemoryViewModel
import com.example.memoryexplorer.ui.screens.home.HomeViewModel
import com.example.memoryexplorer.ui.screens.login.LoginViewModel
import com.example.memoryexplorer.ui.screens.profile.ProfileViewModel
import com.example.memoryexplorer.ui.screens.memorydetails.MemoryDetailsViewModel
import com.example.memoryexplorer.ui.screens.register.RegisterViewModel
import com.example.memoryexplorer.ui.screens.settings.SettingsViewModel
import com.example.memoryexplorer.ui.screens.statistics.StatisticsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("memory-explorer")

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
    single { ThemeRepository(get()) }
    single {
        FavouriteRepository(
            get<MemoryExplorerDatabase>().favouriteDAO()
        )
    }

    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { MemoryDetailsViewModel(get(), get()) }
    viewModel { AddMemoryViewModel(get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { StatisticsViewModel(get(), get()) }

}
