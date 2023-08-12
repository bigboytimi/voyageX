package com.bigboytimi.authenticationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expirationDate;
    private static final int EXPIRATION_TIME = 10;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Token(String token, User user) {
        super();
        this.token = token;
        this.user = user;
        this.expirationDate = this.getTokenExpirationTime();
    }
    public Token(String token) {
        super();
        this.token = token;
        this.expirationDate = this.getTokenExpirationTime();
    }


    private Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }
}
