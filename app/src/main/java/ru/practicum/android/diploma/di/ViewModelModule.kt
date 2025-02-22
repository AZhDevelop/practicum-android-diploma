package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.ui.favorites.FavouritesViewModel
import ru.practicum.android.diploma.ui.search.SearchViewModel
import ru.practicum.android.diploma.ui.vacancy.VacancyViewModel

val viewModelModule = module {
    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        VacancyViewModel(get())
    }

    viewModel<FavouritesViewModel> {
        FavouritesViewModel()
    }

}
