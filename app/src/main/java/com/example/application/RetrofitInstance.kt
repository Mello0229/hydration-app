package com.example.application.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    var authToken: String? = null

    private const val BASE_URL = "https://hydration-backend-copy-production.up.railway.app/"

    private val authInterceptor = Interceptor { chain ->

        val original = chain.request()
        val requestBuilder = original.newBuilder()
//        requestBuilder.addHeader("Authorization", "Bearer ")
        requestBuilder.addHeader("Content-Type", "application/json")

        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
            Log.e("AUTH_TOKEN", "accessToken: [$it]" )
        }
        Log.e("REQUEST_BUILDER", "accessToken: ["+ authToken+"]")
        val request = requestBuilder.build()
        Log.e("REQUEST_BUILDER", "request: "+ request.headers())
        chain.proceed(request)
    }
//    private fun okHttpClient() = OkHttpClient().newBuilder()
//        .addInterceptor(
//            object : Interceptor {
//                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
//                    val request: Request = chain.request()
//                        .newBuilder()
//                        .addHeader("Accept", "application/json")
//                        .addHeader("Authorization", "Bearer $authToken")
//                        .build()
//                    return chain.proceed(request)
//                }
//            }
//        )
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}