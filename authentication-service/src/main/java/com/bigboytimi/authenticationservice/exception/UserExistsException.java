package com.bigboytimi.authenticationservice.exception;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String s) {
        super(s);
    }
}
