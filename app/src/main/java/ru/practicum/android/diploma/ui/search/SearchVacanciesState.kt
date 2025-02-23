package ru.practicum.android.diploma.ui.search

import ru.practicum.android.diploma.domain.models.Vacancies

sealed interface SearchVacanciesState {
    data object Default: SearchVacanciesState
    data object Loading : SearchVacanciesState
    data class Error(val errorCode: Int) : SearchVacanciesState
    data class Content(val data: Vacancies) : SearchVacanciesState
}
