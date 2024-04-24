package com.example.demo.service

import org.springframework.stereotype.Service

@Service
class LogisticService {
    fun getProviders(country: String): List<String>? {
        return when(country) {
            "DE" -> listOf("DHL", "HERMES")
            "FR" -> listOf("DHL")
            else -> null
        }
    }
}