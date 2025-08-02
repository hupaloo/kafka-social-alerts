package dev.hupaloo.exception.twitter;

import org.springframework.http.HttpStatus;

public enum TwitterApiErrorType {

    BAD_REQUEST,
    NOT_FOUND,
    RATE_LIMIT,
    SERVER_ERROR,
    UNKNOWN;

    public static TwitterApiErrorType fromHttpStatus(HttpStatus status) {
        if (status == null) {
            return UNKNOWN;
        }
        return switch (status) {
            case BAD_REQUEST -> BAD_REQUEST;
            case NOT_FOUND -> NOT_FOUND;
            case TOO_MANY_REQUESTS -> RATE_LIMIT;
            case INTERNAL_SERVER_ERROR -> SERVER_ERROR;
            default -> UNKNOWN;
        };
    }
}
