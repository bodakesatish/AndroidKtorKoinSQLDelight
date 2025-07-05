package com.bodakesatish.ktor.domain.usecases

import com.bodakesatish.ktor.domain.model.SchemeModel
import com.bodakesatish.ktor.domain.repository.SchemeRepository
import com.bodakesatish.ktor.domain.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

class GetSchemeListUseCase (
    private val schemeRepository: SchemeRepository
)  {
    suspend operator fun invoke(): Flow<NetworkResult<List<SchemeModel>>> = schemeRepository.observeSchemeList(true)
}