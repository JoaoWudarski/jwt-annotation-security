package com.br.jvcw.domain;

import lombok.Data;

@Data
public abstract class SecureUser {

    private String id;
    private String username;
    private String permissionName;
    private Integer permissionLevel;
}
