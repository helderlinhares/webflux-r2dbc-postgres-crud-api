package me.hl.webfluxapi.exception

data class Error(val code: String?, val message: String?)
data class ErrorResponse(val errors: List<Error>)

class ItemNotFoundException(override val message: String) : RuntimeException()

object ErrorCode {
    const val ITEM_NOT_FOUND = "item_not_found"
    const val FIELD_VALUE_MUST_NOT_BE_BLANK = "field_value_must_not_be_blank"
    const val FIELD_VALUE_TOO_LONG = "field_value_too_long"
    const val UNKNOWN_ERROR = "unknown_error"
}
