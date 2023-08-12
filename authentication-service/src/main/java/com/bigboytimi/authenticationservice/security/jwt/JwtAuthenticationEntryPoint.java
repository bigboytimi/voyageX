package com.bigboytimi.authenticationservice.security.jwt;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final Gson gson;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

         /*
        set the necessary headers
         */
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        /*
        set the responseBody and convert to Json for the api
         */

        final Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        responseBody.put("error", "Unauthorized");
        responseBody.put("message", "You need to authenticate to access this resource.");
        responseBody.put("path", request.getServletPath());

        response.getWriter().write(gson.toJson(responseBody));
    }
}
