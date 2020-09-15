package kr.bistroad.gatewayservice.authorization.presentation

import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class JwtAuthorizationGatewayFilterFactory(
    private val jwtAuthorizationGatewayFilter: JwtAuthorizationGatewayFilter
) : AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig>(NameConfig::class.java) {

    override fun apply(config: NameConfig) = jwtAuthorizationGatewayFilter

}