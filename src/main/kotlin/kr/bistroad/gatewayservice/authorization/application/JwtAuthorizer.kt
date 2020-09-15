package kr.bistroad.gatewayservice.authorization.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kr.bistroad.gatewayservice.authorization.domain.JwtAuthorization
import kr.bistroad.gatewayservice.authorization.infrastructure.JwtInspector
import kr.bistroad.gatewayservice.global.error.exception.InvalidAuthorizationHeaderException
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class JwtAuthorizer(
    private val jwtInspector: JwtInspector
) {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun authorize(request: ServerHttpRequest): JwtAuthorization? {
        val authHeader = request.headers.getFirst(HEADER_NAME)

        if (authHeader != null) {
            if (!authHeader.startsWith(HEADER_VALUE_PREFIX)) throw InvalidAuthorizationHeaderException()

            val token = authHeader.substringAfter(HEADER_VALUE_PREFIX)
            val jws = jwtInspector.inspectToken(token)
            return objectMapper.readValue<JwtAuthorization>(jws.body.subject)
        }
        return null
    }

    companion object {
        const val HEADER_NAME = "Authorization"
        const val HEADER_VALUE_PREFIX = "Bearer "
    }
}