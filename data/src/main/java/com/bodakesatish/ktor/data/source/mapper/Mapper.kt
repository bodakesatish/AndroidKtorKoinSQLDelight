package com.bodakesatish.ktor.data.source.mapper

interface Mapper<Network, Entity, Domain> {
    fun Network.mapToEntity(): Entity
    fun Entity.mapToDomain(): Domain
}