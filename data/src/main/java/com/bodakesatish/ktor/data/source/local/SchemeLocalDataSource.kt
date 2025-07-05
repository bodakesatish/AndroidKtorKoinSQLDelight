package com.bodakesatish.ktor.data.source.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.bodakesatish.ktor.data.SchemeEntityQueries
import com.bodakesatish.ktor.data.source.mapper.SchemeMapper.toDomainModelList
import com.bodakesatish.ktor.data.source.remote.model.SchemeNetworkModel
import com.bodakesatish.ktor.domain.model.SchemeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SchemeLocalDataSource {
    suspend fun insertSchemes(data: List<SchemeNetworkModel>)

    suspend fun insertBatchSchemes(data: List<SchemeNetworkModel>)

    suspend fun count() : Int
    fun selectAll() : Flow<List<SchemeModel>>
}

class SchemeLocalDataSourceImpl(
    private val schemeQueries: SchemeEntityQueries
) : SchemeLocalDataSource {

    override suspend fun insertSchemes(data: List<SchemeNetworkModel>) {
        schemeQueries.transaction {
            data.forEach { entity ->
                schemeQueries.insertOrReplace(
                    schemeCode = entity.schemeCode.toLong(),
                    schemeName = entity.schemeName,
                    lastFetched = System.currentTimeMillis()
                )
            }
        }
    }

    override suspend fun insertBatchSchemes(data: List<SchemeNetworkModel>) {
        val batchSize = 10
        val currentTime = System.currentTimeMillis()

        schemeQueries.transaction { // Perform all operations within a single transaction
            var i = 0
            while (i <= data.size - batchSize) {
                // We have enough items for a batch of 10
                schemeQueries.insertTenSchemes(
                    // Item 1
                    schemeCode1 = data[i].schemeCode.toLong(),
                    schemeName1 = data[i].schemeName,
                    lastFetched1 = currentTime,
                    // Item 2
                    schemeCode2 = data[i + 1].schemeCode.toLong(),
                    schemeName2 = data[i + 1].schemeName,
                    lastFetched2 = currentTime,
                    // Item 3
                    schemeCode3 = data[i + 2].schemeCode.toLong(),
                    schemeName3 = data[i + 2].schemeName,
                    lastFetched3 = currentTime,
                    // Item 4
                    schemeCode4 = data[i + 3].schemeCode.toLong(),
                    schemeName4 = data[i + 3].schemeName,
                    lastFetched4 = currentTime,
                    // Item 5
                    schemeCode5 = data[i + 4].schemeCode.toLong(),
                    schemeName5 = data[i + 4].schemeName,
                    lastFetched5 = currentTime,
                    // Item 6
                    schemeCode6 = data[i + 5].schemeCode.toLong(),
                    schemeName6 = data[i + 5].schemeName,
                    lastFetched6 = currentTime,
                    // Item 7
                    schemeCode7 = data[i + 6].schemeCode.toLong(),
                    schemeName7 = data[i + 6].schemeName,
                    lastFetched7 = currentTime,
                    // Item 8
                    schemeCode8 = data[i + 7].schemeCode.toLong(),
                    schemeName8 = data[i + 7].schemeName,
                    lastFetched8 = currentTime,
                    // Item 9
                    schemeCode9 = data[i + 8].schemeCode.toLong(),
                    schemeName9 = data[i + 8].schemeName,
                    lastFetched9 = currentTime,
                    // Item 10
                    schemeCode10 = data[i + 9].schemeCode.toLong(),
                    schemeName10 = data[i + 9].schemeName,
                    lastFetched10 = currentTime
                )
                i += batchSize
            }

            // Insert any remaining items individually
            while (i < data.size) {
                val entity = data[i]
                schemeQueries.insertOrReplace(
                    schemeCode = entity.schemeCode.toLong(),
                    schemeName = entity.schemeName,
                    lastFetched = currentTime
                )
                i++
            }
        }
    }

    override suspend fun count(): Int {
        return schemeQueries.count().executeAsOne().toInt()
    }

    override fun selectAll() : Flow<List<SchemeModel>> {
        return schemeQueries.selectAll() // This returns a Query<SchemeEntity>
            .asFlow() // Converts the Query into a Flow that emits on changes
            .mapToList(Dispatchers.IO) // Maps the emission to List<SchemeEntity> on the IO dispatcher
            .map { schemeEntities -> // Now, map List<SchemeEntity> to List<SchemeModel>
                // schemeEntities is List<com.bodakesatish.ktor.data.SchemeEntity> (SQLDelight generated type)
                schemeEntities.toDomainModelList() // Your existing extension function
            }
        // No explicit emit or collect here. The map operator transforms the flow.
    }

    //    // Helper to load from cache synchronously (example)
//    private suspend fun loadSchemesFromCache(): List<SchemeNetworkModel> = withContext(Dispatchers.IO) {
//        schemeQueries.selectAll().executeAsList().map { entity ->
//            SchemeNetworkModel(
//                schemeCode = entity.schemeCode.toInt(),
//                schemeName = entity.schemeName,
////                isinGrowth = entity.isinGrowth,
////                isinDivReinvestment = entity.isinDivReinvestment
//            )
//        }
//    }
//
//    suspend fun clearCache() {
//        withContext(Dispatchers.IO) {
//            schemeQueries.deleteAll()
//        }
//    }

}