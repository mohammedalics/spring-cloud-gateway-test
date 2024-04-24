package com.example.demo

import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping
import org.springframework.test.context.ActiveProfiles
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.matching.UrlPattern.ANY

@ActiveProfiles("GeoRoutePredicateFactoryIntegrationSpec")
class GeoRoutePredicateFactoryIntegrationSpec extends AbstractPredicateIntegrationSpec {


    @Unroll
    def "given request from #country by ip #ip, expected route #expectedRoute"() {

        given:
        wireMockServer.stubFor(
                get(ANY).willReturn(aResponse().withStatus(200))
        )

        when:
        def request = webTestClient.get()
        if (ip != null) {
            request.header("X-Forwarded-For", ip)
        }


        def result = request.exchange()

        then:
        result.expectStatus().isEqualTo(200)
                .expectHeader().valueEquals(HANDLER_MAPPER_HEADER, RoutePredicateHandlerMapping.class.getSimpleName())
                .expectHeader().valueEquals(ROUTE_ID_HEADER, expectedRoute)

        where:
        country   | ip              | expectedRoute
        "Germany" | "77.21.147.170" | "route_based_target_predicate"
        "France"  | "103.232.172.0" | "route_based_target_predicate"
        "USA"     | "30.10.0.10"    | "route_others"
        null      | null            | "route_others"
    }
}