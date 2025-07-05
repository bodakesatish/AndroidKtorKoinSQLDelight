package com.bodakesatish.ktor.data.di

import com.bodakesatish.ktor.data.repository.SchemeRepositoryImpl
import com.bodakesatish.ktor.data.source.local.SchemeLocalDataSource
import com.bodakesatish.ktor.data.source.local.SchemeLocalDataSourceImpl
import com.bodakesatish.ktor.data.source.remote.SchemeRemoteDataSource
import com.bodakesatish.ktor.data.source.remote.SchemeRemoteDataSourceImpl
import com.bodakesatish.ktor.domain.repository.SchemeRepository
import org.koin.dsl.module

val dataSourceModuleKoin = module {
   single<SchemeRemoteDataSource> {
        SchemeRemoteDataSourceImpl(
            remoteApiService = get(),
           )
    }
    single<SchemeLocalDataSource> {
        SchemeLocalDataSourceImpl(
            schemeQueries = get()  // Gets SchemeEntityQueries
        )
    }
    single<SchemeRepository> {
        SchemeRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get()
        )
    }
}