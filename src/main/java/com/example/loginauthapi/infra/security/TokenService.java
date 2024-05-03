package com.example.loginauthapi.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.loginauthapi.domain.user.User;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
  @Value("${api.security.token.secret}f")
  private String secret;
  public String generateToken(User user) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      String token = JWT
          .create()
          .withIssuer("login-auth-api")
          .withSubject(user.getEmail())
          .withExpiresAt(generateExpirationDate())
          .sign(algorithm);
      return token;
    } catch (JWTCreationException exception) {
      throw new RuntimeException("Error while trying to authenticate user");
    }
  }

  private Instant generateExpirationDate() {
    LocalDateTime now = LocalDateTime.now();
    ZoneId zone = ZoneId.of("Europe/Paris");
    ZoneOffset zoneOffset = zone.getRules().getOffset(now);
    return now.plusHours(2).toInstant(zoneOffset);
  }

  public String validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT
          .require(algorithm)
          .withIssuer("login-auth-api")
          .build()
          .verify(token)
          .getSubject();
    } catch (JWTVerificationException exception) {
      return null;
    }
  }
}
