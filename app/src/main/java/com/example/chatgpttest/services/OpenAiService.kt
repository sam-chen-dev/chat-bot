package com.example.chatgpttest.services

import android.content.Context
import com.example.chatgpttest.BuildConfig
import com.example.chatgpttest.R
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class OpenAiService(context: Context) {
    private val contentType = "application/json".toMediaType()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val interceptor = Interceptor { chain ->
        val newRequest = chain.request().newBuilder().run {
            addHeader(
                context.getString(R.string.auth_key_header_name),
                context.getString(R.string.auth_key_header_value, BuildConfig.OPENAI_API_KEY)
            )
            build()
        }

        chain.proceed(newRequest)
    }

    private val okHttpClient = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        build()
    }

    private val retrofit = Retrofit.Builder().run {
        addConverterFactory(json.asConverterFactory(contentType))
        client(okHttpClient)
        baseUrl(context.getString(R.string.openai_base_url))
        build()
    }

    val openAiApi: OpenAiApi by lazy {
        retrofit.create(OpenAiApi::class.java)
    }
}