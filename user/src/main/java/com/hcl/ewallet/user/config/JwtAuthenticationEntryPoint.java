package com.hcl.ewallet.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcl.ewallet.user.dto.response.common.ApiResponse;
import com.hcl.ewallet.user.enums.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles unauthenticated requests (missing/invalid token) and returns
 * a standard JSON response instead of Spring Security default HTML.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ApiResponse<Void> api = ApiResponse.error(
                401,
                "Unauthorized - Please login",
                ErrorCode.UNAUTHORIZED.name(),
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(api));
    }
}
