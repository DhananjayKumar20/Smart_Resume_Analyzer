package com.dhananjay.agnhub.exception;

public class JobMatchException extends RuntimeException {
    public JobMatchException(String message) {
        super(message);
    }

    public JobMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
