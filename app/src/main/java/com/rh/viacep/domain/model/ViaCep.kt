package com.rh.viacep.domain.model

data class ViaCep (
    val bairro: String,
    val cep: String,
    val complemento: String,
    val ddd: String,
    val cidade: String,
    val logradouro: String,
    val estado: String
) : java.io.Serializable