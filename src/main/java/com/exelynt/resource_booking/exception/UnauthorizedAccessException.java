package com.exelynt.resource_booking.exception;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException() {
        super();
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}