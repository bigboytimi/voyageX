package com.bigboytimi.authenticationservice.controller;


import com.bigboytimi.authenticationservice.dto.request.LoginRequest;
import com.bigboytimi.authenticationservice.dto.request.RegistrationRequest;
import com.bigboytimi.authenticationservice.dto.response.GlobalResponse;
import com.bigboytimi.authenticationservice.dto.response.LoginResponse;
import com.bigboytimi.authenticationservice.model.Token;
import com.bigboytimi.authenticationservice.repository.TokenRepository;
import com.bigboytimi.authenticationservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserService userService;
    private final TokenRepository tokenRepository;

    @PostMapping("/register-with-email")
    public ResponseEntity<GlobalResponse<String>> registerUser(@RequestBody RegistrationRequest request,
                                                               final HttpServletRequest httpServletRequest){
        GlobalResponse<String> registeredUser = new GlobalResponse<>(userService.registerUser(request, httpServletRequest));
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);

    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token){
        Token savedToken = tokenRepository.findByToken(token);

        if (savedToken.getUser().isEnabled()){
            return "Verified Account: Please proceed to login.";
        }

        String verificationResult = userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("User Token validated successfully")){
            return "Email verification is successful. Proceed to login";
        } else {
            return "Invalid verification";
        }
    }

    @PostMapping("/login-with-email")
    public ResponseEntity<GlobalResponse<LoginResponse>> loginEmail(LoginRequest request){

        GlobalResponse<LoginResponse> authenticatedUser = new GlobalResponse<>(userService.loginUserEmail(request));
        return new ResponseEntity<>(authenticatedUser, HttpStatus.OK);
    }


}
