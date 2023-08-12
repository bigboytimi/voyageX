package com.bigboytimi.authenticationservice.exception;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String pleaseVerifyYourEmail) {
        super(pleaseVerifyYourEmail);
    }
}
