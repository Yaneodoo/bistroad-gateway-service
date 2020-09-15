package kr.bistroad.gatewayservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class BistroadGatewayServiceApplicationTests {

    @Test
    fun contextLoads() {
    }

}
