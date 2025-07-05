package com.bodakesatish.ktor.data.source.mapper

import com.bodakesatish.ktor.data.SchemeEntity
import com.bodakesatish.ktor.data.source.remote.model.SchemeNetworkModel
import com.bodakesatish.ktor.domain.model.SchemeModel

object SchemeMapper : Mapper<SchemeNetworkModel, SchemeEntity, SchemeModel>{
    override fun SchemeNetworkModel.mapToEntity(): SchemeEntity {
        return SchemeEntity(
            schemeCode = schemeCode.toLong(),
            schemeName = schemeName,
            lastFetched = System.currentTimeMillis()
        )
    }

    override fun SchemeEntity.mapToDomain(): SchemeModel {
        return SchemeModel(
            schemeCode = schemeCode,
            schemeName = schemeName
        )
    }

    fun List<SchemeEntity>.toDomainModelList(): List<SchemeModel> {
        return this.map { it.mapToDomain() } // Calls the extension within the object's scope
    }

    fun List<SchemeNetworkModel>.toEntityModelList(): List<SchemeEntity> {
        return this.map { it.mapToEntity() } // Calls the extension within the object's scope
    }

}