package com.bodakesatish.ktor.domain.repository

import com.bodakesatish.ktor.domain.model.SchemeModel
import com.bodakesatish.ktor.domain.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

interface SchemeRepository {
    // Option 1: Return Flow for observable data (recommended for lists)
    fun observeSchemeList(isForceRefresh: Boolean): Flow<NetworkResult<List<SchemeModel>>>
    fun clearCache()
//
//    // Option 2: Suspend function for one-time fetch (if UI doesn't need to observe changes directly)
//    suspend fun getSchemeList(): NetworkResult<List<SchemeModel>>

}