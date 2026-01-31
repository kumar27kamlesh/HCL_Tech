package com.hcl.ewallet.user.config;

import com.hcl.ewallet.user.dto.response.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Wrap all successful controller responses into ApiResponse<T>.
 *
 * Errors are wrapped in GlobalExceptionHandler (also ApiResponse).
 */
@RestControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                 MethodParameter returnType,
                                 MediaType selectedContentType,
                                 Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                 ServerHttpRequest request,
                                 ServerHttpResponse response) {

        // Don't wrap if already ApiResponse
        if (body instanceof ApiResponse) {
            return body;
        }

        // Don't wrap Swagger / static
        if (!MediaType.APPLICATION_JSON.includes(selectedContentType)) {
            return body;
        }

        String path = null;
        if (request instanceof ServletServerHttpRequest servletReq) {
            HttpServletRequest req = servletReq.getServletRequest();
            path = req.getRequestURI();
        }

        int status = HttpStatus.OK.value();
        if (response instanceof ServletServerHttpResponse servletResp) {
            if (servletResp.getServletResponse().getStatus() > 0) {
                status = servletResp.getServletResponse().getStatus();
            }
        }

        String method = null;
        if (request instanceof ServletServerHttpRequest servletReq) {
            method = servletReq.getServletRequest().getMethod();
        }

        String message = resolveSuccessMessage(method, status);

        return ApiResponse.success(status, message, path, body);
    }

    private String resolveSuccessMessage(String method, int status) {
        if (status == HttpStatus.CREATED.value()) return "Created successfully";

        if (method == null) return "Success";
        return switch (method) {
            case "GET" -> "Fetched successfully";
            case "POST" -> "Created successfully";
            case "PUT", "PATCH" -> "Updated successfully";
            case "DELETE" -> "Deleted successfully";
            default -> "Success";
        };
    }
}
