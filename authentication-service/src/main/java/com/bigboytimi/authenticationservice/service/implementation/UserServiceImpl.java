package com.bigboytimi.authenticationservice.service.implementation;

import com.bigboytimi.authenticationservice.dto.request.LoginRequest;
import com.bigboytimi.authenticationservice.dto.request.RegistrationRequest;
import com.bigboytimi.authenticationservice.dto.response.LoginResponse;
import com.bigboytimi.authenticationservice.event.publisher.RegistrationCompleteEvent;
import com.bigboytimi.authenticationservice.exception.InvalidCredentialsException;
import com.bigboytimi.authenticationservice.exception.UnauthorizedUserException;
import com.bigboytimi.authenticationservice.exception.UserExistsException;
import com.bigboytimi.authenticationservice.exception.UserNotFoundException;
import com.bigboytimi.authenticationservice.model.JwtToken;
import com.bigboytimi.authenticationservice.model.Token;
import com.bigboytimi.authenticationservice.model.User;
import com.bigboytimi.authenticationservice.repository.JwtTokenRepository;
import com.bigboytimi.authenticationservice.repository.TokenRepository;
import com.bigboytimi.authenticationservice.repository.UserRepository;
import com.bigboytimi.authenticationservice.security.jwt.JwtUtils;
import com.bigboytimi.authenticationservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtUtils jwtUtils;
    @Override
    public String registerUser(RegistrationRequest request, HttpServletRequest httpServletRequest) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw  new UserExistsException("This email is already used by another user");
        }

        User registeredUser = userRepository.save(User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail()).build());


        publisher.publishEvent(new RegistrationCompleteEvent(registeredUser, getApplicationUrl(httpServletRequest)));

        return "User Registration Completed. Check your email for confirmation";

    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveVerificationToken(User savedUser, String verificationToken) {
        var token = new Token(verificationToken, savedUser);
        tokenRepository.save(token);
    }

    @Override
    public String validateToken(String token) {
        Token tokenFound = tokenRepository.findByToken(token);
        if (tokenFound == null){
            return "Invalid Token";
        }

        User user = tokenFound.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((tokenFound.getExpirationDate().getTime() - calendar.getTime().getTime()) <= 0){
            tokenRepository.delete(tokenFound);
            return "Token is expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "User Token validated successfully";
    }

    @Override
    public LoginResponse loginUserEmail(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("User does not exist. Please register"));

        if (!user.isEnabled()){
            throw new UnauthorizedUserException("Please verify your email");
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())){
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(), user.getPassword());

            List<JwtToken> tokens = jwtTokenRepository.getJwtTokensByUser(user);

            if (!tokens.isEmpty()){
                jwtTokenRepository.deleteAll(tokens);
            }

            String newToken = jwtUtils.generateJwtToken(authentication);

            JwtToken newJwtToken = JwtToken.builder()
                    .accessToken(newToken)
                    .isExpired(false)
                    .isRevoked(false)
                    .user(user)
                    .build();

            return LoginResponse.builder()
                    .message("Hello, "+ user.getFirstName() + ". Please copy your access token")
                    .accessToken(newJwtToken.getAccessToken()).build();

        }
        throw new InvalidCredentialsException("Invalid Credentials");
    }

    public String getApplicationUrl(HttpServletRequest request) {
        return "https://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}
