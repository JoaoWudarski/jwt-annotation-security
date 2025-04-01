package com.br.jvcw.usecase.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.br.jvcw.config.TokenHeader;
import com.br.jvcw.domain.SecureUser;
import com.br.jvcw.domain.TokenProperties;
import com.br.jvcw.exception.InternalSecurityException;
import com.br.jvcw.usecase.SearchUser;
import com.br.jvcw.usecase.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenProperties tokenProperties;
    private final SearchUser searchUser;
    private final TokenHeader tokenHeader;

    @Override
    public String generateToken(SecureUser user) {
        return JWT.create()
                .withIssuer(tokenProperties.getIssuer())
                .withSubject(user.getId())
                .withClaim("body", user.getTokenBody())
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(tokenProperties.getExpirationTimeSeconds() * 60).atZone(ZoneId.systemDefault()).toInstant())
                .sign(Algorithm.HMAC256(tokenProperties.getSecretKey()));
    }

    @Override
    public void validateToken(String token, String permissionLevel) {
        try {
            if (isNull(token))
                throw new InternalSecurityException("Token is null");
            token = token.replace("Bearer ", "");
            String id = JWT.require(Algorithm.HMAC256(tokenProperties.getSecretKey())).withIssuer(tokenProperties.getIssuer()).build().verify(token)
                    .getSubject();

            searchUser.findSecureById(id).filter(userEntity ->
                            userEntity.getPermissionList().contains(permissionLevel))
                    .orElseThrow(() -> new Exception("Invalid user or role"));
            tokenHeader.setValue(token);
        } catch (Exception e) {
            log.error("Error to validate token: {}", e.getMessage(), e);
            throw new InternalSecurityException(e.getMessage());
        }
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Map<String, Object> getTokenBody() {
        return JWT
                .require(Algorithm.HMAC256(tokenProperties.getSecretKey()))
                .withIssuer(tokenProperties.getIssuer()).build()
                .verify(tokenHeader.getValue())
                .getClaim("body")
                .asMap();
    }

    @Override
    public String getUserId() {
        return JWT
                .require(Algorithm.HMAC256(tokenProperties.getSecretKey()))
                .withIssuer(tokenProperties.getIssuer()).build()
                .verify(tokenHeader.getValue())
                .getSubject();
    }
}
