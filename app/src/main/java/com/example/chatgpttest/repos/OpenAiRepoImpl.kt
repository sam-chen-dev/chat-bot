package com.example.chatgpttest.repos

import com.example.chatgpttest.networkModels.CompletedResponse
import com.example.chatgpttest.networkModels.DeltaResponse
import com.example.chatgpttest.networkModels.ResponseDto
import com.example.chatgpttest.networkModels.ResponseEvent
import com.example.chatgpttest.networkModels.ResponseRequest
import com.example.chatgpttest.services.OpenAiApi
import com.example.chatgpttest.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class OpenAiRepoImpl(
    private val openAiApi: OpenAiApi
) : OpenAiRepo {
    private val jsonParser = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun generateResponse(request: ResponseRequest): ResponseDto? {
        val result = openAiApi.generateResponse(request)

        if (!result.isSuccessful) {
            throw Exception("${result.errorBody()?.string()}")
        }

        return result.body()
    }

    override suspend fun generateResponseStream(request: ResponseRequest): Flow<ResponseEvent> {
        return flow {
            openAiApi.generateResponseStream(request).source().use { source ->
                var currentEvent = ""
                var dataJsonString = ""

                while (!source.exhausted()) {
                    val line = source.readUtf8Line() ?: continue

                    when {
                        line.startsWith("event: ") -> {
                            currentEvent = line.removePrefix("event: ")
                        }

                        line.startsWith("data: ") -> {
                            dataJsonString = line.removePrefix("data: ")
                        }

                        line.isBlank() -> {
                            when (currentEvent) {
                                Constants.RESPONSE_EVENT_DELTA -> {
                                    val deltaResponse = jsonParser.decodeFromString<DeltaResponse>(dataJsonString)
                                    emit(ResponseEvent.Delta(deltaResponse.delta))
                                }

                                Constants.RESPONSE_EVENT_COMPLETED -> {
                                    val completedResponse = jsonParser.decodeFromString<CompletedResponse>(dataJsonString)
                                    emit(ResponseEvent.Completed(completedResponse.response.id))
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}