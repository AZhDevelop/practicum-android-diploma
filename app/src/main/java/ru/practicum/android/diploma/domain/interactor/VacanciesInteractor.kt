package ru.practicum.android.diploma.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Industries
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.SearchFilters
import ru.practicum.android.diploma.domain.models.Vacancies
import ru.practicum.android.diploma.domain.models.VacancyDetails

interface VacanciesInteractor {
    fun searchVacancies(params: SearchFilters): Flow<Resource<Vacancies>>
    fun getVacancyDetails(vacancyId: String): Flow<Resource<VacancyDetails>>
    fun getIndustries(): Flow<Resource<Industries>>
}
