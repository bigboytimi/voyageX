package com.bigboytimi.authenticationservice.service;

import com.bigboytimi.authenticationservice.dto.request.LoginRequest;
import com.bigboytimi.authenticationservice.dto.request.RegistrationRequest;
import com.bigboytimi.authenticationservice.dto.response.LoginResponse;
import com.bigboytimi.authenticationservice.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    String registerUser(RegistrationRequest request, HttpServletRequest httpServletRequest);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void saveVerificationToken(User savedUser, String verificationToken);
    String validateToken(String token);
    LoginResponse loginUserEmail(LoginRequest request);
}
