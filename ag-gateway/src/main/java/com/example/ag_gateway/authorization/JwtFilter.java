package com.example.ag_gateway.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@WebFilter(urlPatterns = "/api/**")
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final List<String> excludeUrls = Arrays.asList(
            "/api/users/signup", "/api/users/login",
            "/swagger-ui", "/v3/api-docs",
            "/api/teams/autoPayment", "/api/teams/expelMember",
            "/api/group/backend", "/api/stock/backend",
            "/api/users/valid",
            "/api/auth/health-check", "/api/stocks/health-check", "/api/chat/health-check"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청 URL을 가져와서 제외할 URL 목록과 비교
        String requestURI = request.getRequestURI();
        log.info("[GATEWAY]-jwtFilter : [requestURI] {}", requestURI);

        // login/sign up에 대해서는 필터링을 수행하지 않음
        if (excludeUrls.stream().anyMatch(requestURI::startsWith)) {
            log.info("login/signup/swagger 등에 대해서는 필터링을 수행하지 않음");
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        // authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("authorization이 없거나 Bearer로 시작하지 않음");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Authorization가 필요합니다.");

            return;
        }

        String token = authorization.substring(7);

        // 토큰 만료 검증 - 만료함
        if (jwtUtil.isExpired(token)) {
            log.info("토큰의 유효기간이 만료되었습니다.");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("토큰의 유효기간이 만료되었습니다.");

            return;
        }

        log.info("토큰 검증이 완료되었습니다.");
        filterChain.doFilter(request, response);
    }
}
