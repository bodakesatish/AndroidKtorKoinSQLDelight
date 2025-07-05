package com.bodakesatish.ktor.data.source.remote

import android.util.Log
import com.bodakesatish.ktor.data.source.remote.api.SchemeApiService
import com.bodakesatish.ktor.data.source.remote.model.SchemeNetworkModel
import com.bodakesatish.ktor.domain.utils.NetworkResult
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.json.JSONObject

interface SchemeRemoteDataSource {
    suspend fun fetchSchemeList():  NetworkResult<List<SchemeNetworkModel>>
}

class SchemeRemoteDataSourceImpl (
    private val remoteApiService: SchemeApiService,
    // The @IoDispatcher annotation here is for your documentation/understanding
    // Koin itself won't use it directly unless it's a Koin Qualifier type
) : SchemeRemoteDataSource {

    private val tag = this::class.java.simpleName

    override suspend fun fetchSchemeList():  NetworkResult<List<SchemeNetworkModel>> {
        val networkSchemes = remoteApiService.fetchSchemesFromServer()
        return networkSchemes
    }


    private fun parseErrorMessage(errorBody: String, statusCode: Int, statusDescription: String): String {
        return if (errorBody.isNotEmpty()) {
            try {
                JSONObject(errorBody).getString("message")
            } catch (e: Exception) {
                Log.w(tag, "Failed to parse specific message from error body: $errorBody", e)
                "Error $statusCode: $statusDescription (Raw: $errorBody)"
            }
        } else {
            "Error $statusCode: $statusDescription"
        }
    }

}