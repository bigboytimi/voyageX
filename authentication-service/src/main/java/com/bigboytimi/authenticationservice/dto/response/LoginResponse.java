package com.bigboytimi.authenticationservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginResponse {
    private String message;
    private String accessToken;
}
