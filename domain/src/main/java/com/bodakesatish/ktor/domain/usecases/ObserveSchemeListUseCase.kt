package com.bodakesatish.ktor.domain.usecases

import com.bodakesatish.ktor.domain.model.SchemeModel
import com.bodakesatish.ktor.domain.repository.SchemeRepository
import com.bodakesatish.ktor.domain.utils.NetworkResult

import kotlinx.coroutines.flow.Flow

class ObserveSchemeListUseCase (
    private val schemeRepository: SchemeRepository
)  {
    operator fun invoke(isForceRefresh: Boolean = false): Flow<NetworkResult<List<SchemeModel>>> = schemeRepository.observeSchemeList(isForceRefresh)
}