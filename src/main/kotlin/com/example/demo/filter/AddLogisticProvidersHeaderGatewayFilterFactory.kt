package com.example.demo.filter

import com.example.demo.service.CountryService
import com.example.demo.service.LogisticService
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class AddLogisticProvidersHeaderGatewayFilterFactory(val countryService: CountryService, val logisticService: LogisticService) :
    AbstractGatewayFilterFactory<AddLogisticProvidersHeaderGatewayFilterFactory.Config>(
        Config::class.java
    ) {

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            // Get the client IP address from the request
            val clientIP = exchange.request.headers.getFirst("X-Forwarded-For")
            val country = clientIP?.let { countryService.getCountry(it) }
            val shippingCompanies = country?.let { logisticService.getProviders(it) }
            shippingCompanies?.let {
                val request = exchange.request.mutate().header(config.headerName, it.joinToString(",")).build()
                return@GatewayFilter chain.filter(exchange.mutate().request(request).build())
            }
            chain.filter(exchange)

        }
    }

    class Config(
        val headerName: String,
    )
}
