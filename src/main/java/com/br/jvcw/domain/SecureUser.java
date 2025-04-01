package com.br.jvcw.domain;

import java.util.List;
import java.util.Map;

public interface SecureUser {

    Map<String, Object> getTokenBody();
    String getId();
    List<String> getPermissionList();
}
