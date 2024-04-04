package com.br.jvcw.usecase.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.br.jvcw.domain.SecureUser;
import com.br.jvcw.domain.TokenProperties;
import com.br.jvcw.usecase.SearchUser;
import com.br.jvcw.usecase.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenProperties tokenProperties;
    private final SearchUser searchUser;

    public String generateToken(SecureUser user) {
        return JWT.create()
                .withIssuer(tokenProperties.getIssuer())
                .withSubject(user.getUsername())
                .withClaim("role", user.getPermissionName())
                .withClaim("id", user.getId())
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(tokenProperties.getExpirationTimeSeconds() * 60).toInstant(tokenProperties.getZoneOffset()))
                .sign(Algorithm.HMAC256(tokenProperties.getSecretKey()));
    }

    public void validateToken(String token, Integer permissionLevel) {
        try {
            if (isNull(token))
                throw new Exception("Token is null");
            token = token.replace("Bearer ", "");
            Long id = JWT.require(Algorithm.HMAC256(tokenProperties.getSecretKey())).withIssuer(tokenProperties.getIssuer()).build().verify(token)
                    .getClaim("id").asLong();
            String username = JWT.require(Algorithm.HMAC256(tokenProperties.getSecretKey())).withIssuer(tokenProperties.getIssuer()).build().verify(token)
                    .getSubject();

            searchUser.findById(id).filter(userEntity ->
                            userEntity.getPermissionLevel() > permissionLevel &&
                                    username.equals(userEntity.getUsername()))
                    .orElseThrow(() -> new Exception("Invalid user or role"));
        } catch (Exception e) {
            log.error("Error to validate token: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
