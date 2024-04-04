package com.br.jvcw.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneOffset;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "token-config")
public class TokenProperties {

    private final String issuer;
    private final Long expirationTimeSeconds;
    private final String secretKey;
    private final ZoneOffset zoneOffset;
}
