package com.br.jvcw.usecase;

import com.br.jvcw.domain.SecureUser;

public interface TokenService {

    String generateToken(SecureUser user);
    void validateToken(String token, Integer permissionLevel);
    String generateRefreshToken();
}
