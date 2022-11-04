package com.rh.viacep.data.remote.dto

import com.rh.viacep.domain.model.ViaCep

data class ViaCepDto(
    val bairro: String,
    val cep: String,
    val complemento: String,
    val ddd: String,
    val gia: String,
    val ibge: String,
    val erro : Boolean = false,
    val localidade: String,
    val logradouro: String,
    val siafi: String,
    val uf: String
)

fun ViaCepDto.toViaCep() : ViaCep {
    return ViaCep(
        this.bairro,
        this.cep,
        this.complemento,
        this.ddd,
        this.localidade,
        this.logradouro,
        ufToEstado(this.uf)
    ) 
}

fun ufToEstado(uf:String?) : String{
    return when(uf?.uppercase()){
        "AC" -> "Acre"
        "AL" -> "Alagoas"
        "AP" -> "Amapá"
        "AM" -> "Amazonas"
        "BA" -> "Bahia"
        "CE" -> "Ceará"
        "DF" -> "Distrito Federal"
        "ES" -> "Espírito Santo"
        "GO" -> "Goiás"
        "MA" -> "Maranhão"
        "MT" -> "Mato Grosso"
        "MS" -> "Mato Grosso do Sul"
        "MG" -> "Minas Gerais"
        "PA" -> "Pará"
        "PB" -> "Paraíba"
        "PR" -> "Paraná"
        "PE" -> "Pernambuco"
        "PI" -> "Piauí"
        "RJ" -> "Rio de Janeiro"
        "RN" -> "Rio Grande do Norte"
        "RS" -> "Rio Grande do Sul"
        "RO" -> "Rondônia"
        "RR" -> "Roraima"
        "SC" -> "Santa Catarina"
        "SP" -> "São Paulo"
        "SE" -> "Sergipe"
        "TO" -> "Tocantins"
        else -> ""
    }
}