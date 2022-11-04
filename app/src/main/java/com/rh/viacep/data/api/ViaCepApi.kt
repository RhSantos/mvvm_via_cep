package com.rh.viacep.data.api

import com.rh.viacep.data.remote.dto.ViaCepDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepApi {

    @GET("{CEP}/json/")
    suspend fun getAllAddress(@Path("CEP") cep:String) : Response<ViaCepDto>

}