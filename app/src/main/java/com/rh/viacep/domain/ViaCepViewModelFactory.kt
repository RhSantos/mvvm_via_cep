package com.rh.viacep.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rh.viacep.data.repository.ViaCepRepository

class ViaCepViewModelFactory(private val repository: ViaCepRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViaCepViewModel(repository) as T
    }
}