package com.example.demo.predicate

import com.example.demo.service.CountryService
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.util.Locale
import java.util.function.Predicate

@Component
class GeoRoutePredicateFactory(val countryService: CountryService) :
    AbstractRoutePredicateFactory<GeoRoutePredicateFactory.Config>(Config::class.java) {

    override fun apply(config: Config): Predicate<ServerWebExchange>? {
        return GatewayPredicate { exchange ->
            val countries = config.countries.map { country -> country.uppercase(Locale.getDefault()) }
            if (countries.contains("ALL")) {
                return@GatewayPredicate true
            }

            // Get the client IP address from the request
            val clientIP = exchange.request.headers.getFirst("X-Forwarded-For") ?: "127.0.0.1"
            val country = clientIP.let { countryService.getCountry(clientIP) }
            // Check if at least one delivery option is available for the country
            countries.contains(country?.uppercase(Locale.getDefault()))

        }
    }

    class Config(val countries: List<String>)
}
