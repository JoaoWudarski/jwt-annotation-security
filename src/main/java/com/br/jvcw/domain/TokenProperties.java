package com.br.jvcw.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "token-config")
public class TokenProperties {

    private String issuer;
    private Long expirationTimeSeconds;
    private String secretKey;
}
