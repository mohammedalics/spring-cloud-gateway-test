package com.example.demo


import org.springframework.test.context.ActiveProfiles
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ActiveProfiles("AddLogisticProvidersHeaderGatewayFilterFactoryIntegrationSpec")
class AddLogisticProvidersHeaderGatewayFilterFactoryIntegrationSpec extends AbstractIntegrationSpec {


    public static final String SHIPPING_COMPANIES_RESULT = "shippingCompaniesResult"

    @Unroll
    def "given request from #country by ip #ip, expected shipping providers header existence #expectedHeaderExistence and expected header value #expectedHeaderValue"() {
        given:

        wireMockServer.stubFor(
                any(anyUrl()).atPriority(1)
                        .withHeader("X-Shipping-Providers", matching(".*"))
                        .willReturn(aResponse().withStatus(200).with {
                            if (expectedHeaderValue != null) {
                                it.withHeader(SHIPPING_COMPANIES_RESULT, expectedHeaderValue)
                            }
                            return it
                        }
                        )
        )

        wireMockServer.stubFor(
                any(anyUrl()).atPriority(2)
                        .willReturn(aResponse().withStatus(200))
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
        if (expectedHeaderExistence) {
            result.expectHeader().valueEquals(SHIPPING_COMPANIES_RESULT, expectedHeaderValue)
        } else {
            result.expectHeader().doesNotExist(SHIPPING_COMPANIES_RESULT)
        }

        where:
        country   | ip              | expectedHeaderExistence | expectedHeaderValue
        "France"  | "103.232.172.0" | true                    | "HERMES"
        "Germany" | "77.21.147.170" | true                    | "DHL,HERMES"
        "USA"     | "30.0.0.0"      | false                   | null
    }

}