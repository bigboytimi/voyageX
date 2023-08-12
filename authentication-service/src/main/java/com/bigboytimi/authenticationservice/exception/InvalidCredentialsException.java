package com.bigboytimi.authenticationservice.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String invalidCredentials) {

        super(invalidCredentials);
    }
}
