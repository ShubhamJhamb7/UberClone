package com.uber.uberapi.exceptions;

public class InvalidBookingException extends UberException {
    public InvalidBookingException(String message) {
        super(message);
    }
}
