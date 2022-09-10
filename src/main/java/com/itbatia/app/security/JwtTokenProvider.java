package com.itbatia.app.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long validityInMinutes;

    public String generateToken(String username, String lastName, String role){

        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(validityInMinutes).toInstant());

        return JWT.create()
                .withSubject("User details")
                .withClaim("username", username)
                .withClaim("lastName", lastName)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withIssuer("RestApiAwsS3")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String validateToken(String token) throws JWTVerificationException {

        JWTVerifier verifier = JWT
                .require(Algorithm.HMAC256(secretKey))
                .withSubject("User details")
                .withIssuer("RestApiAwsS3")
                .build();

        DecodedJWT jwt = verifier.verify(token);

        // jwt - это декодированный токен. Из него можно получить всё, что нам надо:
        return jwt.getClaim("username").asString();
    }
}
