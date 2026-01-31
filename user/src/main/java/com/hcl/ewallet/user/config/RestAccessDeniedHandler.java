package com.hcl.ewallet.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcl.ewallet.user.dto.response.common.ApiResponse;
import com.hcl.ewallet.user.enums.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles authenticated but forbidden requests and returns JSON.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ApiResponse<Void> api = ApiResponse.error(
                403,
                "Forbidden - You do not have permission to access this resource",
                ErrorCode.FORBIDDEN.name(),
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(api));
    }
}
