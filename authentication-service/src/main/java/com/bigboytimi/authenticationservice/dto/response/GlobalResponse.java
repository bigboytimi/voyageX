package com.bigboytimi.authenticationservice.dto.response;

import org.springframework.http.HttpStatus;

public class GlobalResponse<T> {
    private String status;
    private String message;
    private T data;

    public GlobalResponse(T data) {
        this.status = HttpStatus.valueOf(HttpStatus.OK.value()).toString();
        this.message = "Request processed successfully";
        this.data = data;
    }
}
