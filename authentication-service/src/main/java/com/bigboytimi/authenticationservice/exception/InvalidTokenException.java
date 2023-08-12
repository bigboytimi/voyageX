package com.bigboytimi.authenticationservice.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String tokenIsExpired) {

        super(tokenIsExpired);
    }
}
