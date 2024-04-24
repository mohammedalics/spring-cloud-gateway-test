package com.example.demo


import org.springframework.test.context.ActiveProfiles
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ActiveProfiles("AddLogisticProvidersHeaderGatewayFilterFactoryIntegrationSpec")
class AddLogisticProvidersHeaderGatewayFilterFactoryIntegrationSpec extends AbstractIntegrationSpec {

    @Unroll
    def "given request from #country by ip #ip, expected status #expectedStatus"() {
        given:

        wireMockServer.stubFor(
                any(anyUrl()).atPriority(1)
                        .withHeader("X-Shipping-Providers", matching(".*"))
                        .willReturn(aResponse().withStatus(200))
        )

        wireMockServer.stubFor(
                any(anyUrl()).atPriority(2)
                        .willReturn(aResponse().withStatus(500))
        )

        when:
        def request = webTestClient.get().uri {
            it.path("/")
            it.build()
        }
        if (ip != null) {
            request.header("X-Forwarded-For", ip)
        }

        def result = request.exchange()

        then:
        result.expectStatus().isEqualTo(expectedStatus)

        where:
        country   | ip              | expectedStatus
        "France"  | "103.232.172.0" | 200
        "Germany" | "77.21.147.170" | 200
        "USA"     | "30.0.0.0"      | 500
    }

}