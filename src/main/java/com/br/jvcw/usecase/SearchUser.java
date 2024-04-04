package com.br.jvcw.usecase;

import com.br.jvcw.domain.SecureUser;

import java.util.Optional;

public interface SearchUser {

    Optional<SecureUser> findById(Object id);
}
