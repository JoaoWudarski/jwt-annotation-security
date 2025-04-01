package com.br.jvcw.usecase;

import com.br.jvcw.domain.SecureUser;

import java.util.Map;

public interface TokenService {

    String generateToken(SecureUser user);
    void validateToken(String token, String permissionLevel);
    String generateRefreshToken();
    Map<String, Object> getTokenBody();
    String getUserId();
}
