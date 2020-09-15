package kr.bistroad.gatewayservice.authorization.presentation

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import kr.bistroad.gatewayservice.authorization.application.JwtAuthorizer
import kr.bistroad.gatewayservice.global.error.exception.BadAuthorizationRequestException
import kr.bistroad.gatewayservice.global.error.exception.InvalidAuthorizationHeaderException
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthorizationGatewayFilter(
    private val jwtAuthorizer: JwtAuthorizer
) : GatewayFilter {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain) =
        Mono.just(exchange.request.mutate())
            .map { builder ->
                builder.headers {
                    it.remove(HEADER_AUTHORIZATION_USER_ID)
                    it.remove(HEADER_AUTHORIZATION_ROLE)
                }

                val authorization = jwtAuthorizer.authorize(exchange.request)
                if (authorization != null) {
                    builder
                        .header(HEADER_AUTHORIZATION_USER_ID, authorization.userId.toString())
                        .header(HEADER_AUTHORIZATION_ROLE, authorization.role)
                }

                builder.build()
            }
            .onErrorMap { err ->
                val message = when (err) {
                    is InvalidAuthorizationHeaderException -> "Invalid Authorization header"
                    is ExpiredJwtException -> "Token expired"
                    is JwtException -> "Invalid token"
                    else -> throw err
                }
                BadAuthorizationRequestException(
                    message,
                    err
                )
            }
            .flatMap {
                chain.filter(
                    exchange.mutate()
                        .request(it)
                        .build()
                )
            }

    companion object {
        const val HEADER_AUTHORIZATION_USER_ID = "Authorization-User-Id"
        const val HEADER_AUTHORIZATION_ROLE = "Authorization-Role"
    }
}