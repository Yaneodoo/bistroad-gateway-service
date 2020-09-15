package kr.bistroad.gatewayservice.authorization.domain

import java.util.*

data class JwtAuthorization(
    val userId: UUID,
    val role: String
)