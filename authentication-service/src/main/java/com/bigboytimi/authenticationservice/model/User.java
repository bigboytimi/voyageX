package com.bigboytimi.authenticationservice.model;

import jakarta.persistence.*;
import jdk.jfr.BooleanFlag;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @NaturalId(mutable = true)
    private String email;
    private String password;
    private Set<Role> role;

    private boolean isEnabled = false;
}
