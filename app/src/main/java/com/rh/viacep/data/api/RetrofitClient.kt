package com.rh.viacep.data.api

import com.google.gson.GsonBuilder
import com.rh.viacep.util.Constants
import com.rh.viacep.data.api.ViaCepApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val retrofit: ViaCepApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepApi::class.java)
    }

}