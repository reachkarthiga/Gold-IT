package com.example.goldit.network

import com.google.firebase.inject.Deferred
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

//private val BASE_URL = "https://run.mocky.io/"
//
//private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//
//private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
//    .addConverterFactory(MoshiConverterFactory.create(moshi))
//    .addCallAdapterFactory(CoroutineCallAdapterFactory())
//    .build()
//
//interface CaratAPIService {
//    @GET("v3/e1325f5c-3178-4a0c-95cf-795e298947b9")
//    suspend fun getProperties() : CaratDataClass
//}
//
//object CaratAPI {
//    val retrofitService: CaratAPIService by lazy {
//        retrofit.create(CaratAPIService::class.java)
//    }
//
//}


private const val BASE_URL = "https://api.metalpriceapi.com/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()

interface CaratAPIService {
    @GET("v1/carat")
    suspend fun getProperties(@Query ("api_key") api_key:String,
                              @Query("base") currency:String,
    ) : CaratDataClass

}

object CaratAPI {
    val retrofitService: CaratAPIService by lazy {
        retrofit.create(CaratAPIService::class.java)
    }

}
