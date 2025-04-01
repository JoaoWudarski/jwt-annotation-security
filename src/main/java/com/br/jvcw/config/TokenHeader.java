package com.br.jvcw.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TokenHeader {

    private String value;
}
