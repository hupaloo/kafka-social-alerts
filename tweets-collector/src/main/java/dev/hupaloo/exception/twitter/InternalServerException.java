package dev.hupaloo.exception.twitter;

public class InternalServerException extends RuntimeException {

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
