package com.rh.viacep.domain

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rh.viacep.data.remote.dto.ViaCepDto
import com.rh.viacep.data.remote.dto.toViaCep
import com.rh.viacep.data.repository.ViaCepRepository
import com.rh.viacep.domain.model.ViaCep
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class ViaCepViewModel(private val repository: ViaCepRepository) : ViewModel() {

    var resultPost : MutableLiveData<Response<ViaCepDto>> = MutableLiveData()

    fun getAllAddress(cep:String){
        viewModelScope.launch {
            val response = repository.getAllAddress(cep)
            resultPost.value = response
        }
    }
}