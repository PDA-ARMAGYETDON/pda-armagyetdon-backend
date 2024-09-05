package com.example.ag_gateway.authorization;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    @Autowired
    public JwtUtil(@Value("${spring.jwt.secret}") String secret){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Boolean isExpired(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload()
                .getExpiration().before(new Date());
    }

}
