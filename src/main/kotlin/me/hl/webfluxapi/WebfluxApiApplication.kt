package me.hl.webfluxapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
import org.springframework.boot.runApplication

// Class excluded so we dont need to override DefaultErrorWebExceptionHandler priority on ExceptionHandler with @Order(-2)
@SpringBootApplication(exclude = [ErrorWebFluxAutoConfiguration::class])
class WebfluxApiApplication

fun main(args: Array<String>) {
	runApplication<WebfluxApiApplication>(*args)
}
