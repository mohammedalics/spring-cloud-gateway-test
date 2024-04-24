package com.example.demo

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.route.Route
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_HANDLER_MAPPER_ATTR
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR


@Import(Config.class)
abstract class AbstractPredicateIntegrationSpec extends AbstractIntegrationSpec {

    protected static final String HANDLER_MAPPER_HEADER = "X-Gateway-Handler-Mapper-Class"
    protected static final String ROUTE_ID_HEADER = "X-Gateway-RouteDefinition-Id"

    @TestConfiguration(proxyBeanMethods = false)
    static class Config {

        @Bean
        @Order(500)
        GlobalFilter modifyResponseFilter() {
            return (exchange, chain) -> {
                String value = exchange.getAttributeOrDefault(GATEWAY_HANDLER_MAPPER_ATTR, "N/A")
                if (!exchange.getResponse().isCommitted()) {
                    exchange.getResponse().getHeaders().add(HANDLER_MAPPER_HEADER, value)
                }
                Route route = exchange.getAttributeOrDefault(GATEWAY_ROUTE_ATTR, null)
                if (route != null) {
                    if (!exchange.getResponse().isCommitted()) {
                        exchange.getResponse().getHeaders().add(ROUTE_ID_HEADER, route.getId())
                    }
                }
                return chain.filter(exchange)
            }
        }
    }
}
