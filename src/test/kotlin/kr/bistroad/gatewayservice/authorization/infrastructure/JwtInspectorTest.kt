package kr.bistroad.gatewayservice.authorization.infrastructure

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = [JwtInspector::class, JwtSigner::class])
internal class JwtInspectorTest {
    @Autowired
    private lateinit var jwtInspector: JwtInspector

    @Autowired
    private lateinit var jwtSigner: JwtSigner

    @Test
    fun `Validates jwt`() {
        val subject = "example-subject"
        val jwt = jwtSigner.createToken(
            subject = subject,
            validTime = 1000L * 60 * 60
        )

        val jws = jwtInspector.inspectToken(jwt)

        jws.body.subject.shouldBe(subject)
    }

    @Test
    fun `Throws error when an expired jwt was given`() {
        val jwt = jwtSigner.createToken(
            subject = "example",
            validTime = -(1000L * 60 * 60)
        )

        shouldThrow<ExpiredJwtException> { jwtInspector.inspectToken(jwt) }
    }

    @Test
    fun `Throws error when an invalid token was given`() {
        val invalidJwt = "iNvAlId_ToKeN"

        shouldThrow<JwtException> { jwtInspector.inspectToken(invalidJwt) }
    }
}