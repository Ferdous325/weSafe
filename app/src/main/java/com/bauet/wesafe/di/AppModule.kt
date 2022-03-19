package com.bauet.wesafe.di

import com.bauet.wesafe.database.AppDatabase
import com.bauet.wesafe.repository.AppRepository
import com.bauet.wesafe.ui.home.FacadeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.invoke(get()) }
    single { AppRepository(get()) }
    viewModel { FacadeViewModel(get()) }
}