package dev.hupaloo.exception.twitter;

import lombok.Getter;

@Getter
public class TwitterApiException extends RuntimeException {

    private final TwitterApiErrorType errorType;
    private final int statusCode;
    private final String requestSummary;
    private final String responseBody;

    public TwitterApiException(TwitterApiErrorType errorType, int statusCode, String requestSummary, String responseBody) {
        super(String.format("%s → %d; body=%s", requestSummary, statusCode, responseBody));
        this.errorType = errorType;
        this.statusCode = statusCode;
        this.requestSummary = requestSummary;
        this.responseBody = responseBody;
    }
}
