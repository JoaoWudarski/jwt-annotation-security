package com.br.jvcw.config;

import com.br.jvcw.annotation.SecurityToken;
import com.br.jvcw.exception.UnauthorizedException;
import com.br.jvcw.usecase.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class RequestInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        SecurityToken securityToken = getSecurityTokenAnnotation(handler);

        if (nonNull(securityToken)) {
            log.trace("Requisicao interceptada, token sendo validado...");
            String token = request.getHeader(securityToken.headerName());
            try {
                Integer permissionLevel = securityToken.minimumLevelRole();
                tokenService.validateToken(token, permissionLevel);
                log.trace("Token {} validado com sucesso!", token);
            } catch (Exception e) {
                log.error(String.format("Requisição não permitida, token %s não autorizado.", token));
                throw new UnauthorizedException(e.getMessage());
            }
        }
        log.trace("Sem segurança para o endpoint!");
        return true;
    }

    private SecurityToken getSecurityTokenAnnotation(Object handler) {
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class<?> beanType = handlerMethod.getBeanType();
            if (beanType.isAnnotationPresent(SecurityToken.class))
                return beanType.getAnnotation(SecurityToken.class);
            else if (handlerMethod.hasMethodAnnotation(SecurityToken.class))
                return handlerMethod.getMethodAnnotation(SecurityToken.class);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
