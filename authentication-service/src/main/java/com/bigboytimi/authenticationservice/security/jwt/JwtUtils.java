package com.bigboytimi.authenticationservice.security.jwt;

import com.bigboytimi.authenticationservice.exception.InvalidTokenException;

import io.jsonwebtoken.*;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    private String generateSecret(){
        return DatatypeConverter.printBase64Binary(new byte[512/8]);
    }
    @Value("${jwt.access-token.expiration}")
    private Long tokenExpirationTimeInMilliseconds;

    private Key generateKey(){
        byte[] secretKeyInBytes = DatatypeConverter.parseBase64Binary(generateSecret());
        return new SecretKeySpec(secretKeyInBytes,"HmacSHA512");
    }

    public String generateJwtToken(Authentication authentication){
        String username = authentication.getName();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+tokenExpirationTimeInMilliseconds))
                .signWith(generateKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUserNameFromToken(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token is expired");
        }
    }

    public boolean isTokenValid(String token){
        try{
            Jwts.parserBuilder().setSigningKey(generateKey()).build().parse(token);
            return true;
        } catch (MalformedJwtException e){
            log.error("Invalid JWT Token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid JWT Token: "+ e.getMessage());
        } catch(ExpiredJwtException e){
            log.error("JWT token is expired: {}", e.getMessage());
            throw new InvalidTokenException("Expired JWT Token: "+ e.getMessage());
        } catch (UnsupportedJwtException e){
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new InvalidTokenException("Unsupported JWT Token: "+ e.getMessage());
        } catch (IllegalArgumentException e){
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new InvalidTokenException("JWT claims is empty: "+ e.getMessage());
        }
    }
}
