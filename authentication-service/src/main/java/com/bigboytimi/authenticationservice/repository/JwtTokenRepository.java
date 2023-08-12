package com.bigboytimi.authenticationservice.repository;

import com.bigboytimi.authenticationservice.model.JwtToken;
import com.bigboytimi.authenticationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    List<JwtToken> getJwtTokensByUser(User user);
}
