package com.hcl.ewallet.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcl.ewallet.user.dto.response.common.ApiResponse;
import com.hcl.ewallet.user.enums.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            // If already authenticated, skip
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // Spring expects "ROLE_X" when using hasRole('X')
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + role));

                User principal = new User(email, "", authorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // ✅ Invalid or expired token -> respond 401 in the standard format
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            String msg = "Invalid or expired token";
            ErrorCode code = ErrorCode.JWT_INVALID;
            String lower = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
            if (lower.contains("expired")) {
                msg = "Token expired";
                code = ErrorCode.JWT_EXPIRED;
            } else if (lower.contains("signature") || lower.contains("malformed") || lower.contains("invalid")) {
                msg = "Invalid token";
                code = ErrorCode.JWT_INVALID;
            }

            ApiResponse<Void> api = ApiResponse.error(401, msg, code.name(), request.getRequestURI());
            response.getWriter().write(objectMapper.writeValueAsString(api));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
