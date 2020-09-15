package kr.bistroad.gatewayservice.authorization

import com.github.tomakehurst.wiremock.client.WireMock.*
import kr.bistroad.gatewayservice.authorization.infrastructure.JwtSigner
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.spec.internal.HttpStatus
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureWebTestClient
internal class AuthorizationIntegrationTests {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var jwtSigner: JwtSigner

    @Test
    fun `Routes with Authorization`() {
        val userId = UUID.randomUUID()
        val token = jwtSigner.createToken(
            subject = """{"userId":"$userId","role":"ROLE_ADMIN"}""",
            validTime = 1000L * 60 * 60
        )

        stubFor(
            delete(urlEqualTo("/users/$userId"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.NO_CONTENT)
                )
        )

        webTestClient
            .delete().uri("/v1/users/$userId")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `Returns forbidden when Authorization is not valid`() {
        val userId = UUID.randomUUID()
        val token = jwtSigner.createToken(
            subject = """{"userId":"$userId","role":"ROLE_ADMIN"}""",
            validTime = -(1000L * 60 * 60)
        )

        webTestClient
            .delete().uri("/v1/users/$userId")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isForbidden
    }
}