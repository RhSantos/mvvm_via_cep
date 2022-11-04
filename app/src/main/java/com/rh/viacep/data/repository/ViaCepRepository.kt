package com.rh.viacep.data.repository

import com.rh.viacep.data.api.RetrofitClient
import com.rh.viacep.data.remote.dto.ViaCepDto
import com.rh.viacep.domain.model.ViaCep
import retrofit2.Call
import retrofit2.Response

class ViaCepRepository {

    suspend fun getAllAddress(cep:String) : Response<ViaCepDto> {
        return RetrofitClient.retrofit.getAllAddress(cep)
    }

}