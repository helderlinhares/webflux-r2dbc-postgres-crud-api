package me.hl.webfluxapi.exception

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.MessageSource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.Locale

@Component
class GlobalErrorHandler (val objectMapper: ObjectMapper, val messageSource: MessageSource) : ErrorWebExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        return when (ex) {
            is ItemNotFoundException -> handleItemNotFoundException(exchange, ex)
            is WebExchangeBindException -> handleWebExchangeBindException(exchange, ex)
            else -> handleUnknownError(exchange)
        }
    }

    private fun handleItemNotFoundException(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        return buildResponseAndLog(
            HttpStatus.NOT_FOUND,
            ErrorResponse(listOf(
                Error(code = ex.message, message = messageSource.getMessage(
                ex.message!!, null, Locale.getDefault()))
            )),
            exchange
        )
    }

    private fun handleWebExchangeBindException(exchange: ServerWebExchange, ex: WebExchangeBindException): Mono<Void> {
        val errors = ex.bindingResult.allErrors.map {
            val args = when (it) {
                is FieldError -> it.rejectedValue?.let { r -> arrayOf(it.field, r) } ?: arrayOf(it.field)
                is ObjectError -> arrayOf(it.objectName)
                else -> emptyArray()
            }
            Error(it.defaultMessage!!, messageSource.getMessage(it.defaultMessage!!, args, Locale.getDefault()))
        }
        return buildResponseAndLog(
            HttpStatus.BAD_REQUEST,
            ErrorResponse(errors),
            exchange
        )
    }

    private fun handleUnknownError(exchange: ServerWebExchange): Mono<Void> {
        return buildResponseAndLog(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorResponse(listOf(
                Error(code = ErrorCode.UNKNOWN_ERROR, message = messageSource.getMessage(
                ErrorCode.UNKNOWN_ERROR, null, Locale.getDefault()))
            )),
            exchange
        )
    }

    private fun buildResponseAndLog(status: HttpStatus, errorResponse: ErrorResponse? = null,
                                    exchange: ServerWebExchange): Mono<Void>{
        val bufferFactory = exchange.response.bufferFactory()
        val dataBuffer: DataBuffer? = try {
            bufferFactory.wrap(objectMapper.writeValueAsBytes(errorResponse))
        } catch (e: JsonProcessingException) {
            bufferFactory.wrap("".toByteArray())
        }
        exchange.response.statusCode = status
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON

        logResponse(status, errorResponse, exchange)

        return exchange.response.writeWith(Mono.just(dataBuffer!!))
    }

    private fun logResponse(status: HttpStatus, errorResponse: ErrorResponse?, exchange: ServerWebExchange) {
        val logString =
            "statusCode=${status.value()}, response=$errorResponse, requestPath=${exchange.request.path}, methodValue=${exchange.request.methodValue}"
        if (status.is5xxServerError)
            logger.error(logString)
        else
            logger.info(logString)
    }
}
