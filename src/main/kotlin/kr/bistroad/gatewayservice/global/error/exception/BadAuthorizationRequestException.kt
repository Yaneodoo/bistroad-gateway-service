package kr.bistroad.gatewayservice.global.error.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class BadAuthorizationRequestException: RuntimeException {
    constructor(message: String, throwable: Throwable): super(message, throwable)
    constructor(throwable: Throwable): super(throwable)
    constructor(): super()
}