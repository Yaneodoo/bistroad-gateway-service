package kr.bistroad.gatewayservice.authorization.application

import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import kr.bistroad.gatewayservice.authorization.infrastructure.JwtInspector
import kr.bistroad.gatewayservice.global.error.exception.InvalidAuthorizationHeaderException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest

internal class JwtAuthorizerTest {
    private val jwtInspector: JwtInspector = mockk()
    private val jwtAuthorizer: JwtAuthorizer = JwtAuthorizer(jwtInspector)

    @Test
    fun `Throws error when an invalid header was given`() {
        val headers = mockk<HttpHeaders>()
        val request = mockk<ServerHttpRequest>()

        every { headers.getFirst("Authorization") } returns "Basic asdf:1234"
        every { request.headers } returns headers

        shouldThrow<InvalidAuthorizationHeaderException> {
            jwtAuthorizer.authorize(request)
        }
    }
}