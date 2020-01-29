package com.example.helloworld.core

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.example.helloworld.BuildConfig
import com.example.helloworld.data.datasources.preference.AppPreference
import com.example.helloworld.data.datasources.preference.AppPreferenceImpl
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkFactory {

    private const val BASE_URL = BuildConfig.BASE_URL
    private const val TIME_OUT = 60L

    fun <Service> createService(appContext: Context, serviceClass: Class<Service>): Service {
        return getRetrofit(
            BASE_URL,
            getOkHttpClient(
                getAuthInterceptor(appContext),
                getLogInterceptors(),
                appContext
            )
        ).create(serviceClass)
    }

    fun getRetrofit(appContext: Context): Retrofit {
        return getRetrofit(
            okHttpClient = getOkHttpClient(
                getAuthInterceptor(appContext),
                getLogInterceptors(),
                appContext
            )
        )
    }

    private fun getRetrofit(baseUrl: String = BASE_URL, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private fun getOkHttpClient(authInterceptor: Interceptor, logInterceptor: Interceptor, context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(object: Authenticator {
                override fun authenticate(route: Route?, response: Response): Request? {
                    return response.request
                }

            })
            // .cache(cache)
            .build()
    }

    private fun getAuthInterceptor(appContext: Context): Interceptor {
        return object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val requestBuilder = chain.request().newBuilder()
//            val token = getSharedPreference(appContext).token
                val token = ""
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", token)
                        .addHeader("Cache-control", "no-cache")
                }

                return chain.proceed(requestBuilder.build())
            }

        }
    }

    private fun getLogInterceptors(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE        }
    }


    private fun getCache(context: Context): Cache {
        val cacheSize: Long = 10 * 1024 * 1024 // 10 MB
        return Cache(context.applicationContext.cacheDir, cacheSize)
    }

    private fun getSharedPreference(appContext: Context) : AppPreference {
        return AppPreferenceImpl(appContext)
    }
}