package com.hcl.ewallet.user.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard API wrapper for success + error responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    /**
     * Stable machine-readable error code (ex: JWT_EXPIRED, ORDER_NOT_FOUND).
     * Null for success responses.
     */
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;

    private T data;
    private Map<String, String> validationErrors;

    public ApiResponse() {
    }

    private ApiResponse(boolean success,
                        int status,
                        String message,
                        String errorCode,
                        String path,
                        T data,
                        Map<String, String> validationErrors) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.data = data;
        this.validationErrors = validationErrors;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(int status, String message, String path, T data) {
        return new ApiResponse<>(true, status, message, null, path, data, null);
    }

    public static <T> ApiResponse<T> error(int status, String message, String path) {
        return new ApiResponse<>(false, status, message, null, path, null, null);
    }

    public static <T> ApiResponse<T> error(int status, String message, String errorCode, String path) {
        return new ApiResponse<>(false, status, message, errorCode, path, null, null);
    }

    public static <T> ApiResponse<T> validationError(int status, String message, String path, Map<String, String> errors) {
        return new ApiResponse<>(false, status, message, "VALIDATION_FAILED", path, null, errors);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public T getData() {
        return data;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}
