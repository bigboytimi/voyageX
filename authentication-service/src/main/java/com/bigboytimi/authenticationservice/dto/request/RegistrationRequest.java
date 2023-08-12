package com.bigboytimi.authenticationservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String password;
    private String email;
}
