package com.example.demo.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

data class CountryResponse(val ip: String, val country: String)

@Service
class CountryService(
    val webClient: WebClient = WebClient.builder().build(),
) {
    fun getCountry(clientIP: String): String? {
        val country = webClient.get().uri("https://api.country.is/$clientIP").retrieve().bodyToMono(String::class.java)
            .map { jacksonObjectMapper().readValue(it, CountryResponse::class.java) }
            .share().block()
        return country?.country
    }
}