package com.jialin.train.gateway.config;

import com.jialin.train.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoginMemberFilter implements GlobalFilter, Ordered {
    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.contains("/admin")
                || path.contains("/hello")
                || path.contains("/redis")
                || path.contains("/member/member/login")
                || path.contains("/member/member/send-code")) {
            LOG.info("No need to verify login {}", path);
            return chain.filter(exchange);
        } else {
            LOG.info("Need to verify login {}", path);
        }

        String token = exchange.getRequest().getHeaders().getFirst("token");
        LOG.info("Verify login token: {}", token);
        if (token == null || token.isEmpty()) {
            LOG.info("token is empty, request is intercepted.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        boolean validate = JwtUtil.validate(token);
        if (validate) {
            LOG.info("Token is valid, request is good.");
            return chain.filter(exchange);
        } else {
            LOG.warn("Token is not valid, request is intercepted.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

    @Override
    public int getOrder() { return 0; }

}
